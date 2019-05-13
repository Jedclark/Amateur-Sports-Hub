<?php
namespace App\Model\Table;

use Cake\ORM\Query;
use Cake\ORM\RulesChecker;
use Hayko\Mongodb\ORM\Table;
use Cake\Validation\Validator;

use Cake\Datasource\ConnectionManager;

class TestTable extends Table
{
	public function initialize(array $config)
        {
		parent::initialize($config);

                $connection = ConnectionManager::get('mongodb');
                $this->setConnection($connection);
		$this->setTable('test');
		//$this->setDisplayField('_id');
		//$this->setPrimaryKey('_id');
	}
}
