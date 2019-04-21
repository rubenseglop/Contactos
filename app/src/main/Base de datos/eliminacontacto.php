<?php

$server = "localhost";
$user = "root";
$pass = "clave";
$bd = "BDSegura";

//Creamos la conexión
$conexion = mysqli_connect($server, $user, $pass,$bd)
or die("Ha sucedido un error inexperado en la conexion de la base de datos");

//generamos la consulta
$idtelefono = $_GET["ID"];

//$sql = "UPDATE MOTORMOT SET ESTADO = 'RESERVADA', CLIENTE = '$cliente '  WHERE MATRICULA = '$matricula'";

$sql = "DELETE FROM USUARIOS WHERE ID = '$idtelefono'";
echo $sql;

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