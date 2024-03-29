<?php

class RealBugAPI{
	
	private $con = null;
	
	private $server = "ec2-54-243-188-54.compute-1.amazonaws.com";
	private	$port = 5482;
	private	$db = "d8253bs8koqicj";
	private	$user = "rmtcunwfewenbn";
	private	$pw = "utQ0pLnQyZTJopK1UX4QSjCMwf";
	
	private $apiAdress = "";
	
	public function __construct(){
		
		try{
			$this->apiAdress = $_SERVER["SERVER_NAME"] . DIRECTORY_SEPARATOR . "index.php";
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
		if(!$connection) throw new Exception("Can�t connect to Database");
		
		$this->con = $connection;
	}
	
	public function getRealBug($id){
		$this->fileLog("retrieve id ".$id);
		\Slim\Slim::getInstance()->response()->header('Content-Type', 'application/json');
		
		try{
			$select = pg_escape_string(sprintf("Select * from bug where id=%d", $id));
			$result = pg_query($this->con, $select);
			
			$bugData = pg_fetch_assoc($result);
			if(!$bugData) throw new Exception(sprintf("No Bug with ID %d", $id));
			
			$return = $this->formatBugData($bugData);
			
			echo json_encode($return);
		}catch(Exception $e){
			echo json_encode(array('error' => $e->getMessage()));
		}
	}
	
	public function getRealBugs(){
		\Slim\Slim::getInstance()->response()->header('Content-Type', 'application/json');
		
		try{
			$select = pg_escape_string("Select * from bug");
			$result = pg_query($this->con, $select);
			
			$returns = array();
			while($bugData = pg_fetch_assoc($result)){
				$returns[] = $this->formatBugData($bugData);
			}
			
			echo json_encode($returns);
		}catch(Exception $e){
			echo json_encode(array('error' => $e->getMessage()));
		}
	}
	
	public function addBug(){
		\Slim\Slim::getInstance()->response()->header('Content-Type', 'application/json');
		
		$this->fileLog("addBug:".file_get_contents("php://input"));
		$data = json_decode(file_get_contents("php://input"), true);
		$this->fileLog("addBug:".var_export($data, true));
		
		$pos = explode(',', $data['position']);
		$description = $data['description'];
		
		$insert = sprintf("INSERT INTO bug (description, lt, ln) VALUES ('%s', %s, %s) Returning id", pg_escape_string($description), $pos[1], $pos[0]);
		$this->fileLog("addBug:".$insert);
		
		$result = pg_query($insert);
		
		$id = pg_fetch_array($result);
		
		echo json_encode(array('id' => $id[0]));
	}
	
	public function updateBugImage($bugId, $data){
		\Slim\Slim::getInstance()->response()->header('Content-Type', 'application/json');
		
		$update = sprintf("update bug set image='%s' where id=%d", pg_escape_bytea($data), $bugId);
		$result = pg_query($update);
		
		if($result === false){
			echo json_encode(array('error' => pg_last_error()));
		}
	}
	
	public function getBugsInEnvironment(){
		$ln = $GET['ln'];
		$ln = $GET['lt'];
		$ln = $GET['radius'];
		
		
	}
	
	private function formatBugData($bugData){
		$imageUrl = $this->apiAdress . DIRECTORY_SEPARATOR . "RealBug" . DIRECTORY_SEPARATOR . $bugData['id'] . DIRECTORY_SEPARATOR . "img";
		$pos = $bugData['ln'] . "," . $bugData['lt'];
		
		return array(
			'id' => $bugData['id'],
			'description' => $bugData['description'],
			'image' => $imageUrl,
			'position' => $pos
		);
	}
	
	public function getImage($id){
		\Slim\Slim::getInstance()->response()->header('Content-Type', 'image/jpeg');
		
		try{
			
			$this->fileLog($_SERVER['CONTENT_LENGTH']);
			$select = pg_escape_string(sprintf("Select * from bug where id=%d", $id));
			$result = pg_query($this->con, $select);
			
			$bugData = pg_fetch_assoc($result);
			if(!$bugData) throw new Exception(sprintf("No Bug with ID %d", $id));
			
			\Slim\Slim::getInstance()->response()->header('Content-Length', strlen($bugData['image']));

			echo pg_unescape_bytea($bugData['image']);
			
		}catch(Exception $e){
			echo json_encode(array('error' => utf8_encode($e->getMessage())));
		}
		
	}
	
	private function fileLog($string){
		
		$handler = fopen(__DIR__."/log/log.txt", "a+");
		fwrite($handler, $string."\n");
		fclose($handler);
	}
	
}

?>