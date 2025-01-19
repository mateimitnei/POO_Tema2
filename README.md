## Proiect Etapa 2 - J. POO Morgan Chase & Co.
### Autor: Matei Mițnei

---

## Structura

### Pachetul `org.poo.system`

Conține clasele principale ale sistemului bancar:

- **Engine**: Clasă singleton care gestionează inițializarea și execuția comenzilor.  
Este responsabilă pentru încărcarea datelor de intrare și executarea comenzilor.
- **Output**: Clasă singleton care gestionează afișarea in fișierul de ieșire.  
Stochează rezultatele execuției comenzilor și le scrie în fișierul transmis in main.
- **User**: Clasa care reprezintă un utilizator al sistemului bancar. Fiecare  
utilizator are un email și o listă de conturi bancare.
- **CommerciantList**: Clasa care deține lista de comercianți preluată din input.  
Este de tip singleton, deci este accesibilă de oriunde din program.
- **ExchangeCurrency**: Clasa care detine lista de "exchange rate-uri" și metoda  
de calcul al exchange-ului.

Acesta conține și următoarele pachete:

### Pachetul `commands`

Pentru execuția comenzilor am folosit design pattern-ul strategy cu ajutorul  
interfeței `Strategy` (cu metoda `execute`) și a clasei `CommandHandler` (cu  
`applyStrategy`).

- Exemplu de clasă care implementeaza interfața Strategy: **Report**: Generează un  
raport pentru un cont bancar specificat cu tranzacțiile  dintr-un interval de  
timestamps.

### Pachetele `transactions`, `cards` și `accounts`

Conține fiecare câte o clasă de tip `factory` pentru crearea ușoară de carduri și  
conturi diferite.

- Ex: **SavingsAccount**: Clasa care reprezintă un cont de economii. Conține  
parametrul `interestRate` pentru calcularea dobânzii.

### Pachetul `splitPayment`

Pentru gestionarea plăților împărțite am folosit design pattern-ul state pentru  
a putea sa schimb starea atunci cand se înregistreaza o acceptare sau o respingere  
a unui utilizator.

- **Payment**: Clasa care reprezintă o astfel de plată. Utilizează state pattern  
pentru a gestiona diferitele stări ale unei plăți (ex: OngoingState, FinalisedState).

### Pachetul `cashback`

Conține interfața `CashbackStrategy` și clasele pentru cele două strategii de  
cashback.

---

## Fluxul programului

În `Main`, se inițializează și se execută "motorul" sistemului bancar folosind  
următorul cod:

```
Output finalOutput = Output.getInstance();
finalOutput.setOutput(objectMapper.createArrayNode());

Engine bankEngine = Engine.getInstance();
bankEngine.init(inputData);
bankEngine.execute();
```

Acest cod inițializează instanța singleton a clasei `Output`, setează un nod JSON  
gol pentru ieșiri, inițializează instanța singleton a clasei `Engine` cu datele  
de intrare și execută comenzile specificate.

---

## Probleme și Soluții

Mi s-a părut dificilă implementarea raportului de tip business. Într-un final am  
găsit o soluție care constă în reținerea  într-un map dublu a fiecărei stări de la  
un anumit timestamp a cheltuielilor și a veniturilor. Ex:  
`Map<Integer, Map<String, Double>> spendingsHistory`.  
Astfel am putut calcula cheltuielile și veniturile dintr-un anumit interval de  
timestamps făcând diferența de sume de la cele două capete de interval.  

---

### Design pattern-uri folosite

- **Singleton**: Pentru clasele `Engine`, `Output`, `CommerciantList`,  
`ExchangeCurrency` și `TheNotFoundError`.
- **Strategy**: Pentru execuția comenzilor.
- **Factory**: Pentru crearea ușoară de carduri, conturi, tranzactii și strategii  
de cashback diferite.
- **State**: Pentru gestionarea plăților de tip "splitPayment".

---
