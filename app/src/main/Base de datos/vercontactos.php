<?php 

$server = "localhost";
$user = "root";
$pass = "clave";
$bd = "BDSegura";

//Creamos la conexión
$conexion = mysqli_connect($server, $user, $pass,$bd) 
or die("Ha sucedido un error inexperado en la conexion de la base de datos");

//generamos la consulta
$sql = "SELECT * FROM USUARIOS";
mysqli_set_charset($conexion, "utf8"); //formato de datos utf8

if(!$result = mysqli_query($conexion, $sql)) die();

$usuarios = array(); //creamos un array

while($row = mysqli_fetch_array($result)) 
{ 
    $id=$row['ID'];
    $nombre=$row['NOMBRE'];    
    $apellidos=$row['APELLIDOS']; 
    $domicilio=$row['DOMICILIO']; 

    $usuarios[] = array('ID' => $id, 'NOMBRE'=> $nombre, 'APELLIDOS'=> $apellidos, 'DOMICILIO'=> $domicilio);

}

//desconectamos la base de datos
$close = mysqli_close($conexion) 
or die("Ha sucedido un error inexperado en la desconexion de la base de datos");
  

//Creamos el JSON
$json_string = json_encode($usuarios);
echo " { usuarios: ".$json_string." }";

//echo $json_string;
//Si queremos crear un archivo json, sería de esta forma:






?>