<?php 

//Creamos la conexión
require_once('dbConnect.php');

//generamos la consulta
$uuid= $_GET["UUIDUNIQUE"];
$sql = "SELECT * FROM CONTACTOS WHERE UUIDUNIQUE='$uuid'";
mysqli_set_charset($conexion, "utf8"); //formato de datos utf8

if(!$result = mysqli_query($conexion, $sql)) die();

$usuarios = array(); //creamos un array

while($row = mysqli_fetch_array($result)) 
{ 
    $id=$row['ID'];
    $foto=$row['FOTO']; 
    $nombre=$row['NOMBRE'];    
    $apellidos=$row['APELLIDOS']; 
    $galeriaid=$row['GALERIAID']; 
    $domicilioid=$row['DOMICILIOID'];
    $telefonoid=$row['TELEFONOID']; 
    $email=$row['EMAIL'];
    $uuid=$row['UUIDUNIQUE'];

    $usuarios[] = array('ID' => $id,'FOTO'=> $foto, 'NOMBRE'=> $nombre, 'APELLIDOS'=> $apellidos, 'GALERIAID'=> $galeriaid, 'DOMICILIOID'=> $domicilioid, 'TELEFONOID'=> $telefonoid,'EMAIL'=> $email, 'UUIDUNIQUE'=> $uuid);

}

//desconectamos la base de datos
$close = mysqli_close($conexion) 
or die("Ha sucedido un error inexperado en la desconexion de la base de datos");
  

//Creamos el JSON
$json_string = json_encode($usuarios);
//echo " { usuarios: ".$json_string." }";

echo $json_string;
//Si queremos crear un archivo json, sería de esta forma:
?>