-- drop table MapIndex;

create table "MapIndex" (
"RoomID" TEXT UNIQUE,
"stIndex" TEXT NOT NULL,
FOREIGN KEY(RoomID) REFERENCES coords(RoomID),
PRIMARY KEY(RoomID)
);

INSERT INTO MapIndex (RoomID, stIndex)
VALUES
(801,"0,1"),
(802,"2,3"),
(803,"4,5"),
(804,"6,7"),
(805,"8,9"),
(806,"10,11"),
(807,"12,13"),
(808,"14,15"),
(809,"16,17"),
(810,"18,19"),
(811,"22"),
(812,"23,24"),
(813,"25,26"),
(814,"27,28"),
(815,"29,30"),
(816,"31,32"),
(817,"33,34"),
(818,"35,36"),
(819,"37,38"),
(820,"39,40"),
("CCSS Faculty","58"),
("LB Student CR Male","20"),
("LB Student CR Female","21"),
("Student Elevator 1","41"),
("Student Elevator 2","42"),
("Faculty Elevator","43"),
("Emergency Exit 1","44"),
("Emergency Exit 2","45"),
("LB Faculty CR Female","46"),
("LB Faculty CR Male","47"),
("CCSS Deans Office","56,57");
