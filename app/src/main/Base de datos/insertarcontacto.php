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
$nombre= $_GET["NOMBRE"];
$apellido= $_GET["APELLIDOS"];
$direccion= $_GET["DOMICILIO"];


  $sql = "INSERT INTO USUARIOS (ID, NOMBRE, APELLIDOS, DOMICILIO) VALUES ('$idtelefono', '$nombre', '$apellido', '$direccion')";

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


<!--
      http://iesayala.ddns.net/BDSegura/insertarcontacto.php?ID=%226%22&NOMBRE=%22ruben%22&APELLIDOS=%22SEGURA%22&DOMICILIO=%22mi%20calle%22
-->

