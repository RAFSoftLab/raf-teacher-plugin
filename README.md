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










