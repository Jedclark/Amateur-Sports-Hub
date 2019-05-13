<?php
namespace App\Controller;

use App\Controller\AppController;

class PerformancesController extends AppController
{
	public function add(){
        $comp = $this->Performances->newEntity();

        if ($this->request->is('post')) {
            $comp = $this->Performances->patchEntity($comp, $this->request->getData());

            if ($this->Performances->save($comp)) {
                echo json_encode(['success' => 1]);
            }
            else
            {
                echo json_encode(['success' => 0]);
            }
        }
        die();
    }
	
	public function find($season_id){
		$query = $this->Performances->find()->where(['season_id = ' => $season_id]);
		echo json_encode($query);
		die();
	}
	
	public function findTeamPerformance($team_id, $season_id){
		$query = $this->Performances->find()
			->where([['season_id = ' => $season_id, 'team_id = ' => $team_id]]);
		echo json_encode($query);
		die();
	}
	
	//$id = performance id
	public function edit($id){
        $performance = $this->Performances->get($id, [
            'contain' => []
        ]);
        if ($this->request->is(['patch', 'post', 'put'])) {
            $performance = $this->Performances->patchEntity($performance, $this->request->getData());
            $this->Performances->save($performance);
        }
		die();
     }
	
}