<?php

class RealBugAPI{
	
	private $con = null;
	
	private $server = "ec2-54-243-188-54.compute-1.amazonaws.com";
	private	$port = 5482;
	private	$db = "d8253bs8koqicj";
	private	$user = "rmtcunwfewenbn";
	private	$pw = "utQ0pLnQyZTJopK1UX4QSjCMwf";
	
	
	public function __construct(){
		
		try{
			$this->connectToDatabase();
		}catch(Exception $e){
			echo json_encode(array('error' => $e->getMessage()));
		}
	}
	
	public function __destruct(){
		pg_close($this->con);
	}
	
	protected function connectToDatabase(){
		$connectionString = sprintf("host=%s port=%d dbname=%s user=%s password=%s sslmode=require options='--client_encoding=UTF8'", $this->server, $this->port, $this->db, $this->user, $this->pw);
		$connection = pg_connect($connectionString);
		if(!$connection) throw new Exception("Can«t connect to Database");
		
		$this->con = $connection;
	}
	
	public function getRealBug($id){
		header('Content-type: application/json');
		
		try{
			$select = pg_escape_string(sprintf("Select * from bug where id=%d", $id));
			$result = pg_query($this->con, $select);
			
			$bugData = pg_fetch_assoc($result);
			if(!$bugData) throw new Exception(sprintf("No Bug with ID %d", $id));
			
			$imageUrl = $_SERVER["REQUEST_URI"] . DIRECTORY_SEPARATOR . "RealBug" . DIRECTORY_SEPARATOR . $bugData['id'] . DIRECTORY_SEPARATOR . "IMG";
			$pos = $bugData['ln'] . "," . $bugData['lt'];
			
			$return = array(
				'id' => $bugData['id'],
				'description' => $bugData['description'],
				'image' => $imageUrl,
				'lnlt' => $pos
			);
			
			echo json_encode($return);
		}catch(Exception $e){
			echo json_encode(array('error' => $e->getMessage()));
		}
	}
	
}

?>