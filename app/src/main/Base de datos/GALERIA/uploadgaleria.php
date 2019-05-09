<?php 
$uuid= $_GET["CARPETAUUID"];

    $carpeta= $uuid;

    if (!file_exists($carpeta)) {
        mkdir($carpeta, 0777, true);
    }

?>