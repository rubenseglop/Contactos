<?php

//Creamos la conexión
require_once('dbConnect.php');


$uuid= $_GET["UUIDUNIQUE"];
$sql = "DELETE FROM CONTACTOS WHERE UUIDUNIQUE='$uuid'";

mysqli_set_charset($conexion, "utf8"); //formato de datos utf8
if (mysqli_query($conexion, $sql)) {
    $sql = "DELETE FROM GALERIA WHERE UUIDUNIQUE='$uuid'";
    if (mysqli_query($conexion, $sql)) {
        $sql = "DELETE FROM DOMICILIO WHERE UUIDUNIQUE='$uuid'";
        if (mysqli_query($conexion, $sql)) {
            $sql = "DELETE FROM TELEFONO WHERE UUIDUNIQUE='$uuid'";
            if (mysqli_query($conexion, $sql)) {
                echo "OK";
            }
        }
    }
} else {
      echo "ERROR";
}

//desconectamos la base de datos
$close = mysqli_close($conexion)
or die("Ha sucedido un error inexperado en la desconexion de la base de datos");

?>