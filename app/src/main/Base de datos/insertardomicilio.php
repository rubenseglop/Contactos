<?php

//Creamos la conexión
require_once('dbConnect.php');

//generamos la consulta
$id = $_GET["ID"];
$direccion= $_GET["DIRECCION"];
$uuid= $_GET["UUIDUNIQUE"];


  $sql = "INSERT INTO DOMICILIO(ID, DIRECCION, UUIDUNIQUE) VALUES ('$id', '$direccion', '$uuid')";

mysqli_set_charset($conexion, "utf8"); //formato de datos utf8
if (mysqli_query($conexion, $sql)) {
      echo "New record created successfully";
} else {
      echo "Error: " . $sql . "<br>" . mysqli_error($conexion);
}

//desconectamos la base de datos
$close = mysqli_close($conexion)
or die("Ha sucedido un error inexperado en la desconexion de la base de datos");

?>