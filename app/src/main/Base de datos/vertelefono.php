<?php 

//Creamos la conexión
require_once('dbConnect.php');

//generamos la consulta
$uuid= $_GET["UUIDUNIQUE"];
$sql = "SELECT * FROM TELEFONO WHERE UUIDUNIQUE='$uuid'";
mysqli_set_charset($conexion, "utf8"); //formato de datos utf8

if(!$result = mysqli_query($conexion, $sql)) die();

$telefono= array(); //creamos un array

while($row = mysqli_fetch_array($result)) 
{ 
    $id=$row['ID'];
    $numero=$row['NUMERO']; 
    $uuid=$row['UUIDUNIQUE'];

    $telefono[] = array('ID' => $id,'NUMERO'=> $numero, 'UUIDUNIQUE'=> $uuid);

}

//desconectamos la base de datos
$close = mysqli_close($conexion) 
or die("Ha sucedido un error inexperado en la desconexion de la base de datos");
  

//Creamos el JSON
$json_string = json_encode($telefono);
//echo " { telefono: ".$json_string." }";

echo $json_string;
//Si queremos crear un archivo json, sería de esta forma:
?>