<?php 
$uuid= $_GET["CARPETAUUID"];

    $carpeta="/galeria";
        echo $carpeta;
        if (!file_exists($carpeta)) {
            mkdir($carpeta, 0777, true);
        }




    $carpeta="/galeria/" . $uuid;
    echo $carpeta;
    if (!file_exists($carpeta)) {
        mkdir($carpeta, 0777, true);
    }

?>