<?php 

 define('HOST','localhost');
 define('USER','root');
 define('PASS','clave');
 define('DB','BDSegura');
 
 $conexion = mysqli_connect(HOST,USER,PASS,DB) 
 or die('Ha sucedido un error inexperado en la conexion de la base de datos');


?>