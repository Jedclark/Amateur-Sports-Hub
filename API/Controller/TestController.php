<?php
namespace App\Controller;

use App\Controller\AppController;

class TestController extends AppController
{
    public function add()
    {
print_r($this->request->getData());

            $data = ['name' => 'bob', 'age' => 32, 'idiot' => false, 'team' => ['name' => 'hello ' . time()]];
            $entity = $this->Test->newEntity($data);
            $saved = $this->Test->save($entity);
            var_export($saved);
    }

    public function test()
    {
        $test = $this->Test->newEntity(['name' => 'bob', 'age' => 32, 'idiot' => false, 'team' => ['name' => 'hello']]);
        $this->Test->save($test);

        $results = $this->Test->find('all', ['conditions' => ['team.name' => 'hello']]);
        print_r($results);

        die();
    }
}
