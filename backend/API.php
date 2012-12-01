<?php

class RealBugAPI{
	
	private $con = null;
	
	private $dbServer = '';
	private $port = '';
	private $dbName = '';
	private $dbUser = '';
	private $dbPassword = '';
	
	
	public function __construct($server, $port, $dbName, $dbUser, $dbPassword){
		
		try{
			$this->dbServer = $server;
			$this->port = $port;
			$this->dbName = $dbName;
			$this->dbUser = $dbUser;
			$this->dbPassword = $dbPassword;
			
			$this->connectToDatabase();
		
		}catch(Exception $e){
			
			echo json_encode(array('error' => $e->getMessage()));
		}
	}
	
	public function __destruct(){
		pg_close($this->con);
	}
	
	protected function connectToDatabase(){
		$connectionString = sprintf("host=%s port=%d dbname=%s user=%s password=%s sslmode=require options='--client_encoding=UTF8'", $this->dbServer, $this->port, $this->dbName, $this->dbUser, $this->dbPassword);
		$connection = pg_connect($connectionString);
		if(!$connection) throw new Exception("Can«t connect to Database");
		
		$this->con = $connection;
	}
	
	public function getRealBug($ID){
		header('Content-type: application/json');
		
		$select = "Select * from bug";
		$result = pg_query($this->con, $select);
		
		$bugData = pg_fetch_assoc($result);
		
		$imageUrl = $_SERVER["REQUEST_URI"] . DIRECTORY_SEPARATOR . "RealBug" . DIRECTORY_SEPARATOR . $bugData['id'] . DIRECTORY_SEPARATOR . "IMG";
		$pos = $bugData['coordination_x'] . "," . $bugData['coordination_y'];
		
		$return = array(
			'id' => $bugData['id'],
			'description' => $bugData['description'],
			'image' => $imageUrl,
			'ltln' => $pos
		);
		
		echo json_encode($return);
	}
	
}

?>