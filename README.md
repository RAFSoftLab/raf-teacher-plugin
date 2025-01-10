<!-- Plugin description -->
**IntelliJ Platform Plugin Template** is a repository that provides a pure template to make it easier to create a new plugin project (check the [Creating a repository from a template][gh:template] article).
The main goal of this template is to speed up the setup phase of plugin development for both new and experienced developers by preconfiguring the project scaffold and CI, linking to the proper documentation pages, and keeping everything organized.
[gh:template]: https://docs.github.com/en/repositories/creating-and-managing-repositories/creating-a-repository-from-a-template
<!-- Plugin description end -->


## Profesor Plugin: Kreiranje i Postavljanje Zadataka

### Opis Funkcionalnosti

Profesor plugin omogućava nastavnicima da automatski kreiraju Git repozitorijume za zadatke i postave ih na server. Studenti mogu preuzeti ove repozitorijume, raditi na njima i push-ovati svoja rešenja nazad na server.

Proces uključuje:
1. Kreiranje novog Git repozitorijuma za zadatke.
2. Podešavanje dozvola na serveru.
3. Kloniranje repozitorijuma na lokalni računar.
4. Dodavanje zadataka u lokalni repozitorijum.
5. Push-ovanje zadataka nazad na server.

---

### Workflow

#### 1. Kreiranje novog Git repozitorijuma

Kreiranje repozitorijuma se vrši pomoću REST API poziva. API kreira direktorijum na serveru, inicijalizuje novi Git repozitorijum i vraća informacije o kreiranom direktorijumu.

**API poziv**:
- **Endpoint**: `/api/v1/directories/create`
- **Zahtev (request)**:
  ```json
  {
      "subject": "OOP",
      "year": "2024_25",
      "testType": "Prvi_ispit",
      "group": "15"
  }
  ```
- **Odgovor (response):
  ```json
  {
      "fullPath": "/srv/git/OOP/2024_25/Prvi_ispit/15/Studentska_resenja",
      "gitInitialized": "true",
      "basePath": "/srv/git/OOP/2024_25/Prvi_ispit/15",
      "status": "success"
  }
  ```

### 2. Podešavanje dozvola na serveru

Skripta na serveru podešava prava pristupa i vlasništvo za novokreirani repozitorijum. Skripta se pokreće pomoću SSH veze iz aplikacije.

**Parametri skripte**:
- **Skripta**: `/lms-api/setup-git-repo.sh`
- **Putanja do repozitorijuma**: `/srv/git/OOP/2024_25/Prvi_ispit/15`

---

### 3. Kloniranje repozitorijuma na lokalni računar

Lokalno kloniranje repozitorijuma omogućava nastavniku da doda zadatke i pripremi sadržaj repozitorijuma.

**Parametri kloniranja**:
- **URL repozitorijuma**: `http://<server-address>/OOP/2024_25/Prvi_ispit/15`
- **Lokalna putanja**: `C:\Projects\GitTest800`

---

### 4. Dodavanje zadataka

Nakon kloniranja, zadaci se dodaju u lokalni direktorijum repozitorijuma (npr. `C:\Projects\GitTest800`).

---

### 5. Push-ovanje zadataka na server

Promene u lokalnom repozitorijumu se push-uju nazad na server kako bi studenti mogli da preuzmu pripremljene zadatke.

**Parametri push-ovanja**:
- **Lokalna putanja**: `C:\Projects\GitTest800`
- **Branch**: `main`
- **Commit poruka**: `"Added task files"`

---

### Napomene

1. **Parametri konfiguracije**:
  - Server adresa, korisnička imena, i lozinke moraju biti definisane u konfiguracionom fajlu.

2. **Testiranje metoda**:
  - Preporučuje se testiranje svake metode pojedinačno kako bi se osigurala ispravnost.


## Profesor Plugin: Upravljanje Studentskim Repozitorijumima

### Preuzimanje Studentskih Rešenja
Plugin omogućava profesorima da preuzmu sva studentska rešenja u jednom koraku i automatski ih organizuju za pregled u IntelliJ IDEA okruženju.

### Funkcionalnosti

#### 1. Preuzimanje Svih Studentskih Repozitorijuma
Metoda `downloadAllStudentWork` omogućava masovno preuzimanje svih studentskih rešenja:

**Parametri**:
- `examPath`: Putanja do ispitnog repozitorijuma (npr. "/OOP/2024_25/Prvi_ispit/15")
- `localBaseDir`: Lokalni direktorijum gde će rešenja biti preuzeta (npr. "C:\Projects\StudentWork")

**Primer korišćenja**:
```
String examPath = "/OOP/2024_25/Prvi_ispit/15";
String localDir = "C:\\Projects\\StudentWork";
downloadAllStudentWork(examPath, localDir);
```

#### 2. Organizacija za IntelliJ IDEA
Metoda `createIntellijProject` automatski kreira strukturu projekta za IntelliJ IDEA:

- Kreira `.idea` direktorijum
- Generiše `modules.xml`
- Kreira `.iml` fajlove za svaki studentski projekat
- Organizuje sve studentske projekte kao module u jednom IntelliJ projektu

---

### Workflow Pregleda Rešenja

#### Preuzimanje rešenja:
1. Pokrenuti `downloadAllStudentWork`
2. Sačekati da se preuzmu svi fajlovi
3. Automatski se kreira IntelliJ struktura projekta

#### Otvaranje u IntelliJ IDEA:
1. Otvoriti **File -> Open**
2. Izabrati direktorijum sa preuzetim rešenjima
3. Sva studentska rešenja će biti dostupna kao zasebni moduli

---

### Prednosti
- Automatizovano preuzimanje svih rešenja
- Organizovana struktura za pregled
- Brz pristup svim studentskim projektima kroz jedan IntelliJ prozor
- Jednostavno kretanje između različitih studentskih rešenja

---

### Napomene

#### Preduslovi:
- Potreban pristup serveru
- Odgovarajuća struktura direktorijuma na serveru
- Studentska rešenja moraju biti u **`Studentska_resenja`** direktorijumu

#### Organizacija:
- Svako studentsko rešenje je zaseban modul
- Zadržava se originalna struktura fajlova
- Podržava standardnu Java projektnu strukturu








