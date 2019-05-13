<?php
namespace App\Controller;

use App\Controller\AppController;

class BasketballsController extends AppController
{
	
	public function findByID($fixture_id){
			$query = $this->Basketballs
				->find('all', ['conditions' => [
					'fixture_id' => intval($fixture_id)
				]]);
			echo json_encode($query);
			die();
		}
	
    public function add(){
        if ($this->request->is('post')) {
            $data = $this->request->getData();
            foreach($data as $key => $val){
                $val = json_decode($val);
                if ( !is_null($val) ){
                    $data[$key] = $val;
                }
            }

            $entity = $this->Basketballs->newEntity();
            $entity = $this->Basketballs->patchEntity($entity, $data);

            if ($this->Basketballs->save($entity)) {
                echo json_encode(['success' => 1]);
            } else {
                echo json_encode(['success' => 0]);
            }
        }

		die();
    }
	
	public function find($season_id, $round){
		$query = $this->Basketballs
			->find('all', ['conditions' => [
				'season_id' => intval($season_id),
				'round' => intval($round)
			]]);
		echo json_encode($query);
		die();
	}
		
	public function findTournamentWinner($season_id, $round){
		$this->loadModel('Teams');
			
		$query = $this->Basketballs
			->find('all', ['conditions' => [
				'season_id' => intval($season_id),
				'round' => intval($round)
			]]);
		$team_id = $query[0]->winner_id;
		$query = $this->Teams->find()->where(['id = ' => $team_id]);
		$results = $query->all();
		$data = $results->toList();
		echo json_encode($data);
		die();
	}
		 
}
