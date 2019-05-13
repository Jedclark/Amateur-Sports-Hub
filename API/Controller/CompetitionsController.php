<?php
namespace App\Controller;

use App\Controller\AppController;

class CompetitionsController extends AppController
{
	public function add(){
        $comp = $this->Competitions->newEntity();

        if ($this->request->is('post')) {
            $team = $this->Competitions->patchEntity($comp, $this->request->getData());
            if ($this->Competitions->save($comp)) {
				
                echo json_encode(['success' => 1]);
            }
            else
            {
                echo json_encode(['success' => 0]);
            }
        }
        die();
    }
	
	//Check if a user can edit a tournament
	public function canEdit($user_id, $comp_id){
		$query = $this->Competitions->find()->where(['id = ' => $comp_id]);
		$result = $query->first();
		if($result->user_id == $user_id){
			echo json_encode(['success' => 1]);
		} else {
			echo json_encode(['success' => 0]);
		}
		die();
	}
	
	/**
	 * $id - The user whose competitions you are finding
	 Need to amend so that it shows all the competitions this team is a part of,
	 user is a part of a team, team is a part of competitions
	 find user's teams -> find team's competitions
	 */
	public function findCompetitions($id){
		$this->loadModel('TeamsUsers');
		$this->loadModel('SeasonsTeams');
		$this->loadModel('CompetitionsSeasons');
		$this->loadModel('Competitions');
		
		//Find the user's teams
		$query = $this->TeamsUsers->find()->where(['user_id = ' => $id]);
		$results = $query->all();
		$data = $results->toList();
		$team_ids = [];
		foreach($data as $d){
			array_push($team_ids, $d->team_id);
		}
		
		//Find the seasons that the team belongs to
		$season_ids = [];
		if(count($team_ids) > 0){
			$query = $this->SeasonsTeams->find()->where(['team_id IN ' => $team_ids]);
			$results = $query->all();
			$data = $results->toList();
			foreach($data as $d){
				array_push($season_ids, $d->season_id);
			}
		}
		
		//Find the competitions that these seasons belong to
		$comp_ids = [];
		if(count($season_ids) > 0){
			$query = $this->CompetitionsSeasons->find()->where(['season_id IN ' => $season_ids]);
			$results = $query->all();
			$data = $results->toList();
			foreach($data as $d){
				array_push($comp_ids, $d->competition_id);
			}
		}
		
		//Find competitions
		if(count($comp_ids) > 0){
			$query = $this->Competitions->find()
				->where(['OR' => [
					'id IN ' => $comp_ids,
					'user_id = ' => $id
				]]);
			$results = $query->all();
			$data = $results->toList();
			echo json_encode($data);
		} else {
			$query = $this->Competitions->find()
				->where(['user_id = ' => $id]);
			$results = $query->all();
			$data = $results->toList();
			echo json_encode($data);
		}
		
		die();
	}
	
	/**
	 * $id - the competition id whose seasons you want to find
	 */
	public function findSeasons($id){
		$this->loadModel('Seasons');
		$query = $this->Seasons->find()->where(['competition_id = ' => $id]);
		$results = $query->all();
		$data = $results->toList();
		
		echo json_encode($data);
		die();
	}
	
}