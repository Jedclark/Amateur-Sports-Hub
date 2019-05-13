<?php
namespace App\Controller;

use App\Controller\AppController;

/**
 * Users Controller
 *
 *
 * @method \App\Model\Entity\User[]|\Cake\Datasource\ResultSetInterface paginate($object = null, array $settings = [])
 */
class UsersController extends AppController
{

	public function findUser(){
		$username = $this->request->getData('username');
		$password = $this->request->getData('password');
		$query = $this->Users->find()->where(['username = ' => $username]);
		$user = $query->first();
		if($user != null){
			if(password_verify($password, $user->password)){
			    $token = uniqid();

				$user_ent = $this->Users->get($user->id, ['contain' => []]);
			    $this->Users->patchEntity($user_ent, ['token' => $token]);
			    $this->Users->save($user_ent);

				echo json_encode([
					'success' => 1,
					'id' => $user->id,
					'token' => $token
				]);

				die();
			}
		}

		echo json_encode(['success' => 0]);
	}

	public function searchUsers($search_query, $logged_in_id, $team_id){
		$this->loadModel('TeamsUsers');

		//remove users already a part of this team
		$query = $this->TeamsUsers->find()
			->where(['team_id' => $team_id]);
		$results = $query->all();
		$data = $results->toList();
		$invalid_user_ids = [];
		foreach($data as $d){
			array_push($invalid_user_ids, $d->user_id);
		}

		$query = $this->Users
			->find('all', ['conditions' => [[
				'username LIKE'=> '%' . $search_query . '%',
				'id != ' => $logged_in_id,
				'id NOT IN ' => $invalid_user_ids
				]]]);
		$results = $query->all();
		$data = $results->toList();

		echo json_encode($data);
		die();
	}

	public function findTeams($id){
		$query = $this->Users->find()->where(['id = ' => $id])->contain(['Teams']);
		$results = $query->all();
		$data = $results->toList();
        echo json_encode($data[0]->teams);
		die();
	}

    /**
     * Index method
     *
     * @return \Cake\Http\Response|void
     */
    public function index()
    {
        $users = $this->paginate($this->Users);

        $this->set(compact('users'));
    }

    /**
     * View method
     *
     * @param string|null $id User id.
     * @return \Cake\Http\Response|void
     * @throws \Cake\Datasource\Exception\RecordNotFoundException When record not found.
     */
    public function view($id = null)
    {
        $user = $this->Users->get($id, [
            'contain' => []
        ]);

        $this->set('user', $user);
    }

    /**
     * Add method
     *
     * @return \Cake\Http\Response|null Redirects on successful add, renders view otherwise.
     */
    public function add()
    {
        $user = $this->Users->newEntity();

        if ($this->request->is('post')) {
            $user = $this->Users->patchEntity($user, $this->request->getData());

            if ($this->Users->save($user)) {
                echo json_encode(['success' => 1]);
            }
            else
            {
                echo json_encode(['success' => 0]);
            }
        }
        die();
    }

    /**
     * Edit method
     *
     * @param string|null $id User id.
     * @return \Cake\Http\Response|null Redirects on successful edit, renders view otherwise.
     * @throws \Cake\Network\Exception\NotFoundException When record not found.
     */
    public function edit($id = null)
    {
        $user = $this->Users->get($id, [
            'contain' => []
        ]);
        if ($this->request->is(['patch', 'post', 'put'])) {
            $user = $this->Users->patchEntity($user, $this->request->getData());
            if ($this->Users->save($user)) {
                $this->Flash->success(__('The user has been saved.'));

                return $this->redirect(['action' => 'index']);
            }
            $this->Flash->error(__('The user could not be saved. Please, try again.'));
        }
        $this->set(compact('user'));
     }

    /**
     * Delete method
     *
     * @param string|null $id User id.
     * @return \Cake\Http\Response|null Redirects to index.
     * @throws \Cake\Datasource\Exception\RecordNotFoundException When record not found.
     */
    public function delete($id = null)
    {
        $this->request->allowMethod(['post', 'delete']);
        $user = $this->Users->get($id);
        if ($this->Users->delete($user)) {
            $this->Flash->success(__('The user has been deleted.'));
        } else {
            $this->Flash->error(__('The user could not be deleted. Please, try again.'));
        }

        return $this->redirect(['action' => 'index']);
    }
}
