db.training_statistics.insertMany(
    [
        {
            trainer_username: 'Sarah.Davis',
            trainer_firstname: 'Sarah',
            trainer_lastname: 'Davis',
            trainer_status: true,
            years_list: [
                {
                    year: 2024,
                    months_list: [
                        {
                            month: 7,
                            trainings_duration: NumberLong('32400000')
                        }
                    ]
                }
            ]
        },
        {
            trainer_username: 'David.Wilson',
            trainer_firstname: 'David',
            trainer_lastname: 'Wilson',
            trainer_status: true,
            years_list: [
                {
                    year: 2024,
                    months_list: [
                        {
                            month: 7,
                            trainings_duration: NumberLong('29160000')
                        }
                    ]
                }
            ]
        },
        {
            trainer_username: 'Laura.Miller',
            trainer_firstname: 'Laura',
            trainer_lastname: 'Miller',
            trainer_status: true,
            years_list: [
                {
                    year: 2024,
                    months_list: [
                        {
                            month: 7,
                            trainings_duration: NumberLong('20520000')
                        }
                    ]
                }
            ]
        },
        {
            trainer_username: 'James.Taylor',
            trainer_firstname: 'James',
            trainer_lastname: 'Taylor',
            trainer_status: true,
            years_list: [
                {
                    year: 2024,
                    months_list: [
                        {
                            month: 7,
                            trainings_duration: NumberLong('17280000')
                        }
                    ]
                }
            ]
        }
    ]
)