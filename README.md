# GMihai: Nota descriere proiect 10

# Management echipa de fotbal
### Lupu Eugen-Petrișor

## Descriere
Aplicatie desktop folosita pentru managementul unei echipa de fotbal, folosita de antrenor pentru:
* administrare jucatori
* programare meciuri si inregistrare rezultate
* inregistrare statistici (goluri, cartonase rosii/galbene, minute jucate)
* gestionarea prezentei la antrenamente
* generare rapoarte si grafice pentru analiza

## Lista principalelor functionalitati
* Gestionare jucatori: adaugare, stergere, editare, listare
* Gestionare meciuri: creare meci, editare, introducere scor
* Inregistrare statistici jucatori pentru fiecare meci
    - goluri
    - pase de gol
    - minute jucate
    - dueluri castigate, pase reusite, mingi salvate (in functie de pozitia din teren a jucatorului)
* Prezenta antrenamente: creare sesiune antrenament, marcare prezenta pentru fiecare jucator
* Rapoarte si statistici: top golgheteri, procent prezenta, posesia medie
* Vizualizari grafice: grafice de evolutie pentru goluri/minute

## Arhitectura
### Clase
![Alt text](documentatie-ghid-utlizare-raport/chart-clase.png)

### Baza de date

1. Tabelul players (Jucători)
Acesta este tabelul de bază care stochează informațiile personale ale membrilor echipei.

id (INT, Primary Key, Auto Increment): Cod unic de identificare pentru fiecare jucător.
first_name (VARCHAR): Prenumele jucătorului.

last_name (VARCHAR): Numele de familie.

position (VARCHAR): Postul pe care joacă (ex: "Portar", "Atacant Central").

number (INT): Numărul de pe tricou (trebuie să fie unic).

2. Tabelul matches (Meciuri)
Stochează calendarul și rezultatele generale ale partidelor, fără detalii despre cine a jucat.

id (INT, Primary Key, Auto Increment): Cod unic al meciului.

opponent (VARCHAR): Numele echipei adverse.

match_date (VARCHAR): Data meciului (format text "zi.luna.an").

location (VARCHAR): Locația ("Acasa" sau "Deplasare").

scored (INT): Golurile marcate de echipa noastră.

received (INT): Golurile primite de la adversari.

3. Tabelul match_stats (Statistici Meci)
Acesta este un tabel de legătură (Many-to-Many) între Jucători și Meciuri. Aici se află detaliile tehnice ("carnetul de note" al meciului).

id (INT, Primary Key): Cod unic al înregistrării.
match_id (INT, Foreign Key): Leagă statistica de un meci din tabelul matches.

player_id (INT, Foreign Key): Leagă statistica de un jucător din tabelul players.

goals (INT): Numărul de goluri marcate de acel jucător în acel meci.

shots_on_target (INT): Șuturi pe spațiul porții.

total_shots (INT): Total șuturi (pe poartă + pe lângă).

passes_completed (INT): Pase reușite.

passes_total (INT): Total pase încercate.

dribbles_completed (INT): Driblinguri reușite.

distance_km (DOUBLE): Distanța alergată (permite zecimale, ex: 10.5 km).

saves (INT): Intervenții salvatoare (specific pentru portari).

tackles (INT): Deposedări (specific pentru apărători).

4. Tabelul trainings (Antrenamente)
Stochează sesiunile de pregătire planificate.

id (INT, Primary Key): Cod unic al antrenamentului.

training_date (VARCHAR): Data antrenamentului.

type (VARCHAR): Tipul (ex: "Fizic", "Tactic", "Recuperare").

duration (INT): Durata în minute.

description (VARCHAR): Descriere opțională sau notele antrenorului.

5. Tabelul training_attendance (Prezență Antrenament)
Acesta este un alt tabel de legătură între Jucători și Antrenamente.

id (INT, Primary Key): Cod unic.

training_id (INT, Foreign Key): Leagă prezența de un antrenament specific.

player_id (INT, Foreign Key): Leagă prezența de un jucător anume.

status (VARCHAR): Starea jucătorului ("Prezent", "Absent", "Invoit", "Accidentat").

rating (INT): Nota acordată de antrenor pentru efortul depus (1-10).

Relatii:  
players (1) — (N) player_statistics  
matches (1) — (N) player_statistics  
training_sessions (1) — (N) attendance  
players (1) — (N) attendance  

## Use cases
- Gestionarea jucatorilor - antrenorul poate adauga/edita/sterge jucatori  
- Planificarea unui meci - antrenorul poate programa un meci: adversar, data/ora, locatie  
- Inregistrarea unui rezultat - antrenorul introduce scorul si statisticile jucatorilor dupa meci
- Antrenament - antrenorul poate organiza o sesiune de antrenament si marcheaza prezenta (+ randamentul jucatorilor eventual)
- Vizualizare rapoarte si statistici - antrenorul poate genera rapoarte (top golgheteri, top marcatori decisivi, prezenta la antrenament)

## Ecranele aplicatiei
- Dashboard principal: lotul de jucatori, meciuri viitoare, etc.
- Players view: lista jucatori + modificari: adaugare jucator, stergere jucator, editare jucator (ex: accidentare, schimbarea pozitiei din teren)
- Matches view: lista meciuri + butoane: adauga meci, editeaza meciul, inregistrare rezultat; form pentru introducarea scorului si a statisticilor jucatorilor
- Training sessions view: crearea unei sesiuni de antrenament, marcarea prezentei (posibil un tabel cu checkbox-uri)
- Reports view: generare top golgheteri, procent prezenta la antrenament, evolutia posesiei pe parcurusul fiecarui meci, etc. 

### Resurse
Markdown Guide, [Online] Available: https://www.markdownguide.org/basic-syntax/ [accesed: Mar 14, 1706]
