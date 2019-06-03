<?php 

//Creamos la conexión
require_once('dbConnect.php');

//generamos la consulta
$sql = "SELECT * FROM USUARIOS_GALERIA";
mysqli_set_charset($conexion, "utf8"); //formato de datos utf8

if(!$result = mysqli_query($conexion, $sql)) die();

$galeria = array(); //creamos un array

while($row = mysqli_fetch_array($result)) 
{ 


    $nombre=$row['NOMBRE'];
    $email=$row['EMAIL']; 
    $foto=$row['FOTO'];
    $uuid=$row['UUIDUNIQUE'];

    $galeria[] = array('NOMBRE' => $nombre,'EMAIL'=> $email, 'FOTO'=> $foto, 'UUIDUNIQUE'=> $uuid);

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