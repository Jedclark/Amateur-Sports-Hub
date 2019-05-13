<?php
namespace App\Controller;

use App\Controller\AppController;

class TeamsController extends AppController
{
    public function add($logged_in_id){
		$this->loadModel('TeamsUsers');
		
        $team = $this->Teams->newEntity();
        if ($this->request->is('post')) {
            $team = $this->Teams->patchEntity($team, $this->request->getData());

            if ($this->Teams->save($team)) {
				$relationship = $this->TeamsUsers->newEntity();
				$relationship->set('user_id', $logged_in_id);
				$relationship->set('team_id', $team->id);
				if($this->TeamsUsers->save($relationship)){
					echo json_encode(['success' => 1]);
				}
            }
            else
            {
                echo json_encode(['success' => 0]);
            }
        }
        die();
    }
	
	public function canEdit($user_id, $team_id){
		$query = $this->Teams->find()->where(['id = ' => $team_id]);
		$result = $query->first();
		if($result->user_id == $user_id){
			echo json_encode(['success' => 1]);
		} else {
			echo json_encode(['success' => 0]);
		}
		die();
	}
	
	public function list(){
        $query = $this->Teams->find('all');
        $results = $query->all();
        $data = $results->toList();

        echo json_encode($data);
        die();
    }
	
	public function searchAllTeams($input, $logged_in_user){
		$this->loadModel('TeamsUsers');
		
		$query = $this->TeamsUsers->find()
			->where(['user_id = ' => $logged_in_user]);
		$results = $query->all();
		$data = $results->toList();
		$invalid_team_ids = [];
		foreach($data as $d){
			array_push($invalid_team_ids, $d->team_id);
		}
		
		if(count($invalid_team_ids) == 0){
			$query = $this->Teams->find('all', [
				'conditions' => [[
					'team_name LIKE' => '%' . $input . '%'
				]
			]]);
			$results = $query->all();
			$data = $results->toList();
		} else {
			$query = $this->Teams->find('all', [
				'conditions' => [[
					'team_name LIKE' => '%' . $input . '%',
					'id NOT IN' => $invalid_team_ids
				]
			]]);
			$results = $query->all();
			$data = $results->toList();
		}
		echo json_encode($data);
		die();
	}
	
	public function searchTeams($input, $sport){
		$query = $this->Teams->find('all', ['conditions' => [['team_name LIKE'=> '%' . $input . '%'], ['sport' => $sport]]]);
		$results = $query->all();
		$data = $results->toList();
		
		echo json_encode($data);
		die();
	}
	
	public function findTeams($id){
		$query = $this->Teams->find()->where(['user_id = ' => $id])->contain(['Teams']);
		$results = $query->all();
		$data = $results->toList();

        echo json_encode($data);
		die();
	}
	
	public function findTeamsPlayingFor($id){
		$this->loadModel('TeamsUsers');
		$query = $this->TeamsUsers->find('all', ['fields' => ['team_id']])->where(['user_id = ' => $id]);
		$ids = [];
		foreach($query as $q){
			array_push($ids, $q->team_id);
		}
		if(count($ids) > 0){
			$query = $this->Teams->find()->where(['id IN' => $ids]);
			$results = $query->all();
			$data = $results->toList();
			echo json_encode($data);
		}
		
		die();
	}
	
	public function findById($id){
		$query = $this->Teams->find()->where(['id = ' => $id]);
		$results = $query->all();
		$data = $results->toList();

        echo json_encode($data);
		die();
	}
	
	/* ($team_id, $user_id), is('get') */
	public function joinTeam(){
		$this->loadModel('TeamsUsers');
		$relationship = $this->TeamsUsers->newEntity();
        if ($this->request->is('post')) {
            $relationship = $this->TeamsUsers->patchEntity($relationship, $this->request->getData());
            if ($this->TeamsUsers->save($relationship)) {
                echo json_encode(['success' => 1]);
            }
            else
            {
                echo json_encode(['success' => 0]);
            }
        }
        die();
	}
	
	/* A list of teams the user plays for */
	public function teamsPlayingFor($user_id){
		$this->loadModel('TeamsUsers');
		$query = $this->TeamsUsers->find()->where(['user_id = ' => $user_id]);
		$results = $query->all();
		$data = $results->toList();
		
		echo json_encode($data);
		die();
	}
	
	//Need to return 2 teams
	public function findHomeAwayTeams($homeTeamID, $awayTeamID){
		$ids = [$homeTeamID, $awayTeamID];
		$query = $this->Teams->find()->where(['id IN ' => $ids]);
		$results = $query->all();
		$data = $results->toList();
		
		echo json_encode($data);
		die();
	}
	
	//$id = team id
	public function findTeamsSeasons($id){
		$this->loadModel('SeasonsTeams');
		$this->loadModel('Seasons');
		
		$query = $this->SeasonsTeams->find()
			->select('season_id')
			->where(['team_id = ' => $id]);
		$results = $query->all();
		$data = $results->toList();
		
		$season_ids = [];
		foreach($data as $d){
			array_push($season_ids, $d->season_id);
		}
		
		$query = $this->Seasons->find()
			->join([
				'table' => 'competitions',
				'alias' => 'Competitions',
				'type' => 'INNER',
				'conditions' => 'Competitions.id = Seasons.competition_id'
			])
			->select($this->Seasons)
			->select('Competitions.sport')
			->select('Competitions.type')
			->where(['Seasons.id IN ' => $season_ids]);
		$results = $query->all();
		$data = $results->toList();
		foreach($data as $d){
			$d->set('sport', $d->Competitions['sport']);
			$d->set('type', $d->Competitions['type']);
			unset($d->Competitions);
		}
		
		echo json_encode($data);
		die();
	}
	
}