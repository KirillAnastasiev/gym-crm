use test

db.createCollection('training_statistics', {
    validator: {
        $jsonSchema: {
            bsonType: 'object',
            title: 'Training summary statistics schema',
            required: [
                'trainer_username',
                'trainer_firstname',
                'trainer_lastname',
                'trainer_status',
                'years_list'
            ],
            properties: {
                trainer_username: {
                    bsonType: 'string'
                },
                trainer_firstname: {
                    bsonType: 'string'
                },
                trainer_lastname: {
                    bsonType: 'string'
                },
                trainer_status: {
                    bsonType: 'bool'
                },
                years_list: {
                    bsonType: 'array',
                    items: {
                        bsonType: 'object',
                        required: [
                            'year',
                            'months_list'
                        ],
                        properties: {
                            year: {
                                bsonType: 'int',
                                minimum: 2000,
                                maximum: 3000
                            },
                            months_list: {
                                bsonType: 'array',
                                items: {
                                    bsonType: 'object',
                                    required: [
                                        'month',
                                        'trainings_duration'
                                    ],
                                    properties: {
                                        month: {
                                            bsonType: 'int',
                                            minimum: 1,
                                            maximum: 12
                                        },
                                        trainings_duration: {
                                            bsonType: 'long',
                                            minimum: 0
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
})

db.training_statistics.createIndex({ trainer_username: 1 }, { name: 'trainer_username_idx', unique: true })
db.training_statistics.createIndex({ trainer_firstname: 1 }, { name: 'trainer_firstname_idx' })
db.training_statistics.createIndex({ trainer_lastname: 1 }, { name: 'trainer_lastname_idx' })

