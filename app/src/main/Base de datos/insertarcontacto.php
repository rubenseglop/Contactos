<?php

//Creamos la conexión
require_once('dbConnect.php');

//generamos la consulta
$idtelefono = $_GET["ID"];
$foto= $_GET["FOTO"];
$nombre= $_GET["NOMBRE"];
$apellido= $_GET["APELLIDOS"];
$galeriaid= $_GET["GALERIAID"];
$direccionid= $_GET["DOMICILIOID"];
$telefonoid= $_GET["TELEFONOID"];
$email= $_GET["EMAIL"];
$uuid= $_GET["UUIDUNIQUE"];


  $sql = "INSERT INTO USUARIOS (ID, FOTO, NOMBRE, APELLIDOS, GALERIAID, DOMICILIOID, TELEFONOID, EMAIL, UUIDUNIQUE) VALUES ('$idtelefono', '$foto', '$nombre', '$apellido', '$galeriaid' , '$direccionid', '$telefonoid', '$email', '$uuid')";

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