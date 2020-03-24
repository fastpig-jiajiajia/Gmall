package com.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gmall.annotations.LoginRequired;
import com.gmall.entity.OmsCartItem;
import com.gmall.entity.PmsSkuInfo;
import com.gmall.service.CartService;
import com.gmall.service.SkuService;
import com.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;

@RestController
public class CartController {

    @Reference
    SkuService skuService;

    @Reference
    CartService cartService;


    /**
     * 内嵌结算页面更新状态，并结算
     * @param isChecked
     * @param skuId
     * @param request
     * @param response
     * @param session
     * @param modelMap
     * @return
     */
    @RequestMapping("checkCart")
    @LoginRequired(loginSuccess = true)
    public ModelAndView checkCart(String isChecked, String skuId,HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap modelMap) {

        String memberId = "1";
        // 更新商品的状态
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setIsChecked(isChecked);
        cartService.checkCart(omsCartItem);

        List<OmsCartItem> omsCartItemList = cartService.cartList(memberId);
        modelMap.put("cartList", omsCartItemList);

        BigDecimal totalAmount = getTotalAmount(omsCartItemList);
        modelMap.put("totalAmount", totalAmount);

        return new ModelAndView("cartListInner");
    }


    /**
     * 购物车已选商品价格计算
     * 实现思路，如果用户已经登录，将数据库中的购物车商品信息取出进行相加
     * 否则取出Cookie 中的值 进行相加，
     * 先计算单种商品的总价，然后进行已选商品的总价计算
     * @param request
     * @param response
     * @param session
     * @param modelMap
     * @return
     */
    @RequestMapping(value = {"cartList", "cartList.html"})
    @LoginRequired(loginSuccess = false)
    public ModelAndView cartList(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap modelMap) {
        List<OmsCartItem> omsCartItemList = new ArrayList<>();
        String memberId = (String) request.getAttribute("memberId");

        // 获取cookie 中的值
        String cookieCart = CookieUtil.getCookieValue(request, "cartListCookie", false);

        if("1".equals(memberId)){
           List<OmsCartItem> cookieCartList = new ArrayList<>();
            if(StringUtils.isNotBlank(cookieCart)){
                cookieCartList = JSON.parseArray(cookieCart, OmsCartItem.class);
                cartService.mergeCart(cookieCartList, memberId);
                CookieUtil.deleteCookie(request, response, "cartListCookie");
            }
            omsCartItemList = cartService.cartList(memberId);
        }else{
            if(StringUtils.isNotBlank(cookieCart)){
                omsCartItemList = JSON.parseArray(cookieCart, OmsCartItem.class);
            }
        }

        // 计算单个商品总价，数量*价格
        for (OmsCartItem omsCartItem : omsCartItemList) {
            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
        }

        BigDecimal totalMoney = getTotalAmount(omsCartItemList);
        modelMap.put("cartList", omsCartItemList);
        modelMap.put("totalAmount", totalMoney);

        return new ModelAndView("cartList");
    }


    /**
     * 实现思路，如果用户没有登陆，那么用户浏览器的 Cookie，如果Cookie中存在改商品，那么将Cookie中的值进行相加，
     * 否则在Cookie中新增的数据，重新写入Cookie
     * 用户已经登录，如果已经存在了，数量进行相加，否则新增商品信息到数据库中，并刷新redis缓存，重新写入。
     * Cookie 是浏览器的副本，session是服务器的，直接赋值，无需set
     * @param skuId
     * @param quantity
     * @param request
     * @param response
     * @param session
     * @return
     */
    @RequestMapping(value={"addToCart"})
    @LoginRequired(loginSuccess = false)
    public ModelAndView addToCart(String skuId, int quantity, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        List<OmsCartItem> omsCartItemList = new ArrayList<>();

        // 从数据库中获取该商品的信息
        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId, "");

        // 将信息封装成购物车对象
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setModifyDate(new Date());
        omsCartItem.setPrice(pmsSkuInfo.getPrice());
        omsCartItem.setProductAttr("");
        omsCartItem.setProductBrand("");
        omsCartItem.setProductCategoryId(pmsSkuInfo.getCatalog3Id());
        omsCartItem.setProductId(pmsSkuInfo.getProductId());
        omsCartItem.setProductName(pmsSkuInfo.getSkuName());
        omsCartItem.setProductPic(pmsSkuInfo.getSkuDefaultImg());
        omsCartItem.setProductSkuCode("11111111111");
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setQuantity(new BigDecimal(quantity));

        // 取出从前台返回的用以判断用户是否登陆的标识
        String memberId = "1";

        // 如果用户没登陆
        if(StringUtils.isBlank(memberId)){
            String cookie = CookieUtil.getCookieValue(request, "cartListCookie", false);

            // cookie 为空
            if(!StringUtils.isNotBlank(cookie)){
                omsCartItemList.add(omsCartItem);
            }else{
                // cookie 不为空，查询判断Cookie 中是否存在商品
                boolean flag = if_cart_exist(omsCartItemList, omsCartItem);
                if(flag){
                    // 存在购物车内，合并数量
                    // 将 Cookie 转为数据
                    omsCartItemList = JSON.parseArray(cookie, OmsCartItem.class);
                    for (OmsCartItem cartItem : omsCartItemList) {
                        if(cartItem.getProductSkuId().equals(skuId)) {
                            cartItem.setQuantity(omsCartItem.getQuantity());
                        }
                    }
                }else{
                    omsCartItemList.add(omsCartItem);
                }
            }

            // 将信息重新写入Cookie，返回给浏览器。
            CookieUtil.setCookie(request, response, "cartListCookie", JSONObject.toJSONString(omsCartItemList),
                    60*60*72, false);

        }else{
            // 从数据库中根据 id 取出购物车的数据
            OmsCartItem omsCartItemFromDb = cartService.ifCartExistByUser(memberId,skuId);

            // 如果数据库中存在数据，就更新
            if(omsCartItemFromDb != null){
                omsCartItemFromDb.setQuantity(omsCartItemFromDb.getQuantity().add(omsCartItem.getQuantity()));
                cartService.updateCart(omsCartItemFromDb);
            }else{
                omsCartItem.setMemberId(memberId);
                omsCartItem.setProductId(skuId);
                omsCartItem.setQuantity(new BigDecimal(quantity));
                cartService.addCart(omsCartItem);
            }

            // 刷新缓存
            cartService.flushCartCache(memberId);

        }

        return new ModelAndView("redirect:success.html");
    }

    /**
     * 结算
     */
    @RequestMapping("toTrade")
    @LoginRequired(loginSuccess = true)
    public ModelAndView toTrade(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap modelMap){
        String memberId = (String) request.getAttribute("memberId");
        String userName = (String) request.getAttribute("userName");
        return new ModelAndView("toTrade");
    }



    /**
     * 判断新添加的物品是否存在于购物车内
     * @param omsCartItems
     * @param omsCartItem
     * @return
     */
    private boolean if_cart_exist(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem) {
        boolean b = false;
        for (OmsCartItem cartItem : omsCartItems) {
            String productSkuId = cartItem.getProductSkuId();
            if (productSkuId.equals(omsCartItem.getProductSkuId())) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 根据商品列表，计算已选商品价格
     * @param omsCartItems
     * @return
     */
    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsCartItem omsCartItem : omsCartItems) {
            BigDecimal totalPrice = omsCartItem.getTotalPrice();

            if(omsCartItem.getIsChecked().equals("1")){
                totalAmount = totalAmount.add(totalPrice);
            }
        }
        return totalAmount;
    }
}
