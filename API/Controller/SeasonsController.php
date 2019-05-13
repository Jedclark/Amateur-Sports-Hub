<?php
namespace App\Controller;

use App\Controller\AppController;

class SeasonsController extends AppController
{
	
	public function add(){
		$this->loadModel('CompetitionsSeasons');
        $season = $this->Seasons->newEntity();

        if ($this->request->is('post')) {
            $season = $this->Seasons->patchEntity($season, $this->request->getData());

            if ($this->Seasons->save($season)) {
				$relationship = $this->CompetitionsSeasons->newEntity();
				$relationship->competition_id = $season->competition_id;
				$relationship->season_id = $season->id;
				$this->CompetitionsSeasons->save($relationship);
				echo json_encode(['success' => 1]);
            }
            else
            {
                echo json_encode(['success' => 0]);
            }
        }
        die();
    }
	
	public function removeTeamFromSeason(){
		$this->loadModel('SeasonsTeams');
		
		if($this->request->is('post')){
			$season_id = $this->request->getData('season_id');
			$team_id = $this->request->getData('team_id');
			
			$query = $this->SeasonsTeams->find()
				->where([[
					'season_id = ' => $season_id,
					'team_id = ' => $team_id
				]]);
			$result = $query->first();
			
			if($this->SeasonsTeams->delete($result)){
				echo json_encode(['success' => 1]);
			} else {
				echo json_encode(['success' => 0]);
			}
		}
		die();
	}
	
	public function addTeamToSeason(){
		$this->loadModel("SeasonsTeams");
		$relationship = $this->SeasonsTeams->newEntity();
		
		if($this->request->is('post')){
			$relationship = $this->SeasonsTeams->patchEntity($relationship, $this->request->getData());
			if($this->SeasonsTeams->save($relationship)){
				echo json_encode(['success' => 1]);
			} else {
				echo json_encode(['success' => 0]);
			}
		}
		die();
	}
	
	public function findTeams($id, $sport){
		$this->loadModel('SeasonsTeams');
		$this->loadModel('Teams');
		$query = $this->SeasonsTeams->find()->where(['season_id = ' => $id]);
		$team_ids = [];
		foreach($query as $q){
			array_push($team_ids, $q->team_id);
		}
		
		if(count($team_ids) > 0){
			$query = $this->Teams->find()->where([['id IN ' => $team_ids], ['sport = ' => $sport]]);
			$results = $query->all();
			$data = $results->toList();
			echo json_encode($data);
		}
		
		die();
	}
	
	public function edit($id){
        $season = $this->Seasons->get($id, [
            'contain' => []
        ]);
        if ($this->request->is(['patch', 'post', 'put'])) {
            $season = $this->Seasons->patchEntity($season, $this->request->getData());
            $this->Seasons->save($season);
        }
		die();
     }
	
}