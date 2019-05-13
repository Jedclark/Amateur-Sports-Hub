<?php
namespace App\Controller;

use Cake\Controller\Controller;
use Cake\Event\Event;

class AuthController extends AppController
{
    public function beforeFilter(\Cake\Event\Event $event)
    {
        $this->loadModel('Users');
        if ( isset($this->request->getHeader('X-TOKEN')[0]) )
        {
			$token = $this->request->getHeader('X-TOKEN')[0];
			$query = $this->Users->find()->where(['token = ' => $token]);
			$user = $query->first();
			if ( is_null($user) )
			{
				die();
			}
		}
		else
		{
		    die();
		}
    }
}
