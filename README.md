# Welcome to the Hotel

### Reference Documentation
This application is created by Igor Rodrigues Pessoa
to Hotel and provides multiple APIs to be used by the IT personal.
The rules are:
* The Hotel contains only 1 room;
* Your stay can’t be longer than 3 days;
* The room can’t be reserved more than 30 days in advance;
* All reservations start at least the next day of booking;
* A “DAY’ in the hotel room starts from 00:00 to 23:59:59.

Contains the following options:
- [CREATE](#create) - Create a reservation!
- [CHECK](#check) - Consult a reservation through Reservation Code!
- [LIST](#list) - List all reservations!
- [CANCEL](#cancel) - Cancel a reservation!
- [UPDATE](#update) - Update the period of your reservation!
- [AVAILABILITY](#availability) - Check the availability of a period!

## CREATE

You can book using the room through ```POST```: 
```localhost:8080/hotel/reservation```

In the body you will have to define a ```startDate``` and an ```endDate``` as the exemple:

```
{
    "startDate": "2021-07-29"
    "endDate": "2021-07-30",
}
```

You will receive in the response a link with your reservation code.

## CHECK
Check your reservation through a ```GET``` at the ```localhost:8080/hotel/reservation/{reservation_code}``` endpoint.

You should set ```startDate``` and ```endDate``` in the body of your request, and the ```reservation_code``` in the endpoint .

## LIST
Check the room reservations through a ```GET``` at the ```localhost:8080/hotel/reservations``` endpoint.

You will be able to see all the reservations and see if there is a date available for you.

## CANCEL
Cancel your reservation using the Reservation Code. You should use ```DELETE```
to the reservation endpoint ```localhost:8080/hotel/reservation/{reservation_code}```,
setting your reservation code in the endpoint.

## UPDATE
You can update the ```startDate``` and ```endDate``` using ```PUT```.
You will change your stay in the Room.

## AVAILABILITY
Last but not least you can use the ```localhost:8080/hotel/availability``` endpoint
to check if the room is available in the period sent, using ```startDate``` and ```endDate``` in the body of your request
