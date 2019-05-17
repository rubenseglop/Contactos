<?php
 
 if($_SERVER['REQUEST_METHOD']=='POST'){
 

 $uuid= $_POST['UUID'];
 $path = $_POST['PATH'];

 $imagen= $_POST['FOTO'];

 require_once('dbConnect.php');
 
 $sql ="SELECT ID FROM FOTOS ORDER BY id ASC";
 
 $res = mysqli_query($conexion,$sql);
 
 $id = 0;
 
 while($row = mysqli_fetch_array($res)){
 $id = $row['ID'];
 }
 
 $url = "fotosperfiles/$id.png";
 
 $actualurl = "http://iesayala.ddns.net/BDSegura/misContactos/$url";
 
 $sql = "INSERT INTO FOTOS (UUID,path,PATH) VALUES ('$uuid', '$actualurl','$path')";
 
 if(mysqli_query($conexion,$sql) || $path!="http://iesayala.ddns.net/BDSegura/misContactos/fotosperfiles/perfil.png"){
 file_put_contents($url,base64_decode($imagen));
 echo "Subio imagen Correctamente";
 }
 
 mysqli_close($conexion);
 }else{
 echo "Error";
 }


?>