SELECT CONCAT(
 '*10\r\n',
   '$', LENGTH(redis_cmd), '\r\n', redis_cmd, '\r\n',
   '$', LENGTH(redis_key), '\r\n', redis_key, '\r\n',
   '$', LENGTH(hkey1),'\r\n',hkey1,'\r\n',
   '$', LENGTH(hval1),'\r\n',hval1,'\r\n',
   '$', LENGTH(hkey2),'\r\n',hkey2,'\r\n',
   '$', LENGTH(hval2),'\r\n',hval2,'\r\n',
   '$', LENGTH(hkey3),'\r\n',hkey3,'\r\n',
   '$', LENGTH(hval3),'\r\n',hval3,'\r\n',
   '$', LENGTH(hkey4),'\r\n',hkey4,'\r\n',
   '$', LENGTH(hval4),'\r\n',hval4,'\r'
)
FROM (
 SELECT
 'HSET' AS redis_cmd,
 CONCAT('order:info:',orderid) AS redis_key,
 'ordertime' AS hkey1, ordertime AS hval1,
 'ordermoney' AS hkey2, ordermoney AS hval2,
 'orderstatus' AS hkey3, orderstatus AS hval3,
 'version' AS hkey4, `version` AS hval4
 FROM `order`
) AS t

