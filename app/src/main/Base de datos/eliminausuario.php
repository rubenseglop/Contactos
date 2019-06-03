<?php

//Creamos la conexión
require_once('dbConnect.php');

//generamos la consulta
$uuid= $_GET["UUIDUNIQUE"];


$sql = "DELETE FROM USUARIOS_GALERIA WHERE UUIDUNIQUE= '$uuid'";
echo $sql;

mysqli_set_charset($conexion, "utf8"); //formato de datos utf8
if (mysqli_query($conexion, $sql)) {
      echo "OK";
} else {
      echo "ERROR";
}

//desconectamos la base de datos
$close = mysqli_close($conexion)
or die("Ha sucedido un error inexperado en la desconexion de la base de datos");

?>