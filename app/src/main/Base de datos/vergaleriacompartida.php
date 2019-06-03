<?php 

//Creamos la conexión
require_once('dbConnect.php');

//generamos la consulta
$uuid= $_GET["IDUSUARIO"];
 
$sql = "SELECT * FROM GALERIA WHERE UUIDUNIQUE='$uuid'";

mysqli_set_charset($conexion, "utf8"); //formato de datos utf8

if(!$result = mysqli_query($conexion, $sql)) die();

$galeria = array(); //creamos un array

while($row = mysqli_fetch_array($result)) 
{ 
    $id=$row['IDUSUARIO'];
    $path=$row['PATH']; 
    $uuid=$row['UUIDUNIQUE'];

    $galeria[] = array('IDUSUARIO' => $id,'PATH'=> $path, 'UUIDUNIQUE'=> $uuid);

}

//desconectamos la base de datos
$close = mysqli_close($conexion) 
or die("Ha sucedido un error inexperado en la desconexion de la base de datos");
  

//Creamos el JSON
$json_string = json_encode($galeria);
//echo " { galeria: ".$json_string." }";

echo $json_string;
//Si queremos crear un archivo json, sería de esta forma:
?>