<?php

//Creamos la conexión
require_once('dbConnect.php');

$sql = "DELETE FROM USUARIOS ";
mysqli_set_charset($conexion, "utf8"); //formato de datos utf8
if (mysqli_query($conexion, $sql)) {
    $sql = "DELETE FROM GALERIA";
    if (mysqli_query($conexion, $sql)) {
        $sql = "DELETE FROM DOMICILIO ";
        if (mysqli_query($conexion, $sql)) {
            $sql = "DELETE FROM TELEFONO";
            if (mysqli_query($conexion, $sql)) {
                $sql = "DELETE FROM FOTOS";
                if (mysqli_query($conexion, $sql)) {
                    $sql = "ALTER TABLE FOTOS AUTO_INCREMENT = 1";
                    if (mysqli_query($conexion, $sql)) {
                        unlink('fotosperfiles\0.png');
                        echo "New record created successfully";
                    }
                }
            }
        }
    }
} else {
      echo "Error: " . $sql . "<br>" . mysqli_error($conexion);
}

//desconectamos la base de datos
$close = mysqli_close($conexion)
or die("Ha sucedido un error inexperado en la desconexion de la base de datos");

?>