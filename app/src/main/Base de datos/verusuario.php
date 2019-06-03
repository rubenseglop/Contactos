<?php 

//Creamos la conexión
require_once('dbConnect.php');

//generamos la consulta
$uuid= $_GET["UUIDUNIQUE"];
$sql = "SELECT * FROM USUARIOS_GALERIA WHERE UUIDUNIQUE='$uuid'";
mysqli_set_charset($conexion, "utf8"); //formato de datos utf8

if(!$result = mysqli_query($conexion, $sql)) die();

$usuario= array(); //creamos un array

while($row = mysqli_fetch_array($result)) 
{ 
    $nombre=$row['NOMBRE'];
    $email=$row['EMAIL'];
    $foto=$row['FOTO']; 
    $uuid=$row['UUIDUNIQUE'];
    $usuario[] = array('NOMBRE' => $nombre,'EMAIL'=> $email,'FOTO'=> $foto, 'UUIDUNIQUE'=> $uuid);

}

//desconectamos la base de datos
$close = mysqli_close($conexion) 
or die("Ha sucedido un error inexperado en la desconexion de la base de datos");
  

//Creamos el JSON
$json_string = json_encode($usuario);
//echo " { usuario: ".$json_string." }";

echo $json_string;
//Si queremos crear un archivo json, sería de esta forma:
?>