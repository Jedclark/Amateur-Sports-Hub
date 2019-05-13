<?php
namespace App\Controller;

use App\Controller\AppController;

class FixturesController extends AuthController
{
	public function add()
    {
        $fixture = $this->Fixtures->newEntity();

        if ($this->request->is('post')) {
            $fixture = $this->Fixtures->patchEntity($fixture, $this->request->getData());

            if ($this->Fixtures->save($fixture)) {
                echo json_encode(['success' => 1]);
            }
            else
            {
                echo json_encode(['success' => 0]);
            }
        }
        die();
    }

	public function find($id){
		$this->loadModel('Teams');

		$query = $this->Fixtures
			->find('all', ['order' => 'round'])
			->where([['season_id = ' => $id, 'scores_entered' => '0']]);
		$results = $query->all();
		$data = $results->toList();

		foreach($data as $d){
			$team_ids = [$d->home_team_id, $d->away_team_id];
			$query = $this->Teams->find()
				->where(['id IN ' => $team_ids]);
			$results = $query->all();
			$team_data = $results->toList();
			$d->set('teams', $team_data);
			unset($d->Teams);
		}

		echo json_encode($data);
		die();
	}

	public function findAllGames($id){
		$this->loadModel('Teams');

		$query = $this->Fixtures
			->find('all', ['order' => 'round'])
			->where([['season_id = ' => $id]]);
		$results = $query->all();
		$data = $results->toList();

		foreach($data as $d){
			$team_ids = [$d->home_team_id, $d->away_team_id];
			$query = $this->Teams->find()
				->where(['id IN ' => $team_ids]);
			$results = $query->all();
			$team_data = $results->toList();
			$d->set('teams', $team_data);
			unset($d->Teams);
		}

		echo json_encode($data);
		die();
	}

	public function findGamesByRound($season_id, $round){
		$this->loadModel('Teams');

		$query = $this->Fixtures->find()
			->where([[
				'season_id = ' => $season_id,
				'round = ' => $round],
				'scores_entered' => '0']);
		$results = $query->all();
		$data = $results->toList();

		foreach($data as $d){
			$team_ids = [$d->home_team_id, $d->away_team_id];
			$query = $this->Teams->find()
				->where(['id IN ' => $team_ids]);
			$results = $query->all();
			$team_data = $results->toList();
			$d->set('teams', $team_data);
			unset($d->Teams);
		}

		echo json_encode($data);
		die();
	}

	public function findById($id){
		$query = $this->Fixtures->find()->where(['id = ' => $id]);
		$results = $query->all();
		$data = $results->toList();
		echo json_encode($data);
		die();
	}

	//$id = season id
	public function findGamesInSeason($id){
		$this->loadModel('Teams');
		$this->loadModel('Seasons');
		$this->loadModel('Competitions');

		$query = $this->Fixtures
			->find('all', ['order' => 'round'])
			->where([['season_id = ' => $id]]);
		$results = $query->all();
		$data = $results->toList();
		foreach($data as $d){
			$ids = [$d->home_team_id, $d->away_team_id];
			$query = $this->Teams->find()
				->where(['OR' => ['id IN ' => $ids, 'id IN ' => $ids]]);
			$results = $query->all();
			$team_data = $results->toList();
			$d->set('teams', $team_data);

			$query = $this->Seasons->find()
				->select('competition_id')
				->where(['id = ' => $d->season_id]);
			$result = $query->first();
            $comp_id = $result->competition_id;

			$query = $this->Competitions->find()
				->select('type')
				->where(['id = ' => $comp_id]);
			$result = $query->first();
			$type = $result->type;
			$d->set('type', $type);
			$d['date'] = date('d/m/Y, H:i', strtotime($d['date']));

			unset($d->Competitions);
			unset($d->Teams);
		}
		echo json_encode($data);
		die();
	}

	//$id = User id
	public function findMyGames($id){
		$this->loadModel('TeamsUsers');
		$this->loadModel('Teams');
		$this->loadModel('Seasons');

		//Find the teams the user plays for, add to array
		$query = $this->TeamsUsers->find('all', ['fields' => ['team_id']])->where(['user_id = ' => $id]);
		$ids = [];
		foreach($query as $q){
			array_push($ids, $q->team_id);
		}

		if(count($ids) > 0){
			$query = $this->Fixtures
				->find('all')
				->join([
					'table' => 'seasons',
					'alias' => 'Seasons',
					'type' => 'INNER',
					'conditions' => 'Seasons.id = Fixtures.season_id'
				])
				->join([
					'table' => 'competitions',
					'alias' => 'Competitions',
					'type' => 'INNER',
					'conditions' => 'Seasons.competition_id = Competitions.id'
				])
				->select($this->Fixtures)
				->select('Seasons.competition_id')
				->select('Competitions.sport')
				->select('Competitions.type')
				->where(['OR' => ['home_team_id IN ' => $ids, 'away_team_id IN ' => $ids]])
				->order(['date' => 'ASC']);
			$results = $query->all();
			$data = $results->toList();
			foreach($data as $d){
				$d['date'] = date('d/m/Y, H:i', strtotime($d['date']));
				$d->set('sport', $d->Competitions['sport']);
				$d->set('type', $d->Competitions['type']);
				$d->set('competition_id', $d->Seasons['competition_id']);
				$team_ids = [$d['home_team_id'], $d['away_team_id']];
				$query = $this->Teams->find()->where(['id IN ' => $team_ids]);
				$results = $query->all();
				$team_data = $results->toList();
				$d->set('teams', $team_data);
				unset($d->Competitions);
				unset($d->Seasons);
			}
			echo json_encode($data);
		}

		die();
	}

	//$id = team_id
	public function findTeamsFixturesLimit3($id){
		$this->loadModel('Teams');

		$query = $this->Fixtures
			->find()
			->join([
				'table' => 'seasons',
				'alias' => 'Seasons',
				'type' => 'INNER',
				'conditions' => 'Seasons.id = Fixtures.season_id'
			])
			->join([
				'table' => 'competitions',
				'alias' => 'Competitions',
				'type' => 'INNER',
				'conditions' => 'Seasons.competition_id = Competitions.id'
			])
			->select($this->Fixtures)
			->select('Seasons.competition_id')
			->select('Competitions.sport')
			->select('Competitions.type')
			->where(['OR' => ['home_team_id' => $id, 'away_team_id' => $id]])
			->limit(3)
			->order(['date' => 'ASC']);
		$results = $query->all();
		$data = $results->toList();

		foreach($data as $d){
			$team_ids = [$d['home_team_id'], $d['away_team_id']];
			if($id == $d['home_team_id']){
				$query = $this->Teams->find()->where(['id = ' => $d['away_team_id'] ]);
				$results = $query->all();
				$team_data = $results->toList();
				$d->set('opponent', $team_data);
			} else {
				$query = $this->Teams->find()->where(['id = ' => $d['home_team_id'] ]);
				$results = $query->all();
				$team_data = $results->toList();
				$d->set('opponent', $team_data);
			}
			$d['date'] = date('d/m/Y', strtotime($d['date']));
			$d->set('type', $d->Competitions['type']);

			$query = $this->Teams->find()
				->select('address')
				->select('town')
				->select('post_code')
				->where(['id = ' => $d['home_team_id']]);
			$results = $query->all();
			$game_address = $results->toList();
			$d->set('address', $game_address[0]->address);
			$d->set('town', $game_address[0]->town);
			$d->set('post_code', $game_address[0]->post_code);

			unset($d->Competitions);
			unset($d->Seasons);
		}

		echo json_encode($data);
		die();
	}

	//$id = team_id
	public function findAllTeamsFixtures($id){
		$this->loadModel('Teams');

		$query = $this->Fixtures
			->find()
			->join([
				'table' => 'seasons',
				'alias' => 'Seasons',
				'type' => 'INNER',
				'conditions' => 'Seasons.id = Fixtures.season_id'
			])
			->join([
				'table' => 'competitions',
				'alias' => 'Competitions',
				'type' => 'INNER',
				'conditions' => 'Seasons.competition_id = Competitions.id'
			])
			->select($this->Fixtures)
			->select('Seasons.competition_id')
			->select('Competitions.sport')
			->select('Competitions.type')
			->where(['OR' => ['home_team_id' => $id, 'away_team_id' => $id]])
			->order(['date' => 'ASC']);
		$results = $query->all();
		$data = $results->toList();

		foreach($data as $d){
			$team_ids = [$d['home_team_id'], $d['away_team_id']];
			if($id == $d['home_team_id']){
				$query = $this->Teams->find()->where(['id = ' => $d['away_team_id'] ]);
				$results = $query->all();
				$team_data = $results->toList();
				$d->set('opponent', $team_data);
			} else {
				$query = $this->Teams->find()->where(['id = ' => $d['home_team_id'] ]);
				$results = $query->all();
				$team_data = $results->toList();
				$d->set('opponent', $team_data);
			}
			$d['date'] = date('d/m/Y', strtotime($d['date']));
			$d->set('type', $d->Competitions['type']);

			$query = $this->Teams->find()
				->select('address')
				->select('town')
				->select('post_code')
				->where(['id = ' => $d['home_team_id']]);
			$results = $query->all();
			$game_address = $results->toList();
			$d->set('address', $game_address[0]->address);
			$d->set('town', $game_address[0]->town);
			$d->set('post_code', $game_address[0]->post_code);

			unset($d->Competitions);
			unset($d->Seasons);
		}

		echo json_encode($data);
		die();
	}

	//$id = fixture id
	public function edit($id){
		$fixture = $this->Fixtures->get($id, [
            'contain' => []
        ]);
        if ($this->request->is(['patch', 'post', 'put'])) {
            $fixture = $this->Fixtures->patchEntity($fixture, $this->request->getData());
            if ($this->Fixtures->save($fixture)){
                echo json_encode(['success' => 1]);
            } else {
                echo json_encode(['success' => 0]);
            }
        }
		die();
	}

}
