// ================= VARIABLES =================

let account = null;

let balance = 0;

let totalDeposit = 0;

let totalWithdraw = 0;

let dailyWithdraw = 0;

let transactions = [];

const DAILY_LIMIT = 20000;


// ================= CREATE ACCOUNT =================

document
    .getElementById("accountForm")
    .addEventListener("submit", function(event) {

        event.preventDefault();

        const name =
            document.getElementById("name").value;

        const phone =
            document.getElementById("phone").value;

        const email =
            document.getElementById("email").value;

        const initialDeposit =
            Number(
                document.getElementById("initialDeposit").value
            );


        // Create account number

        const accountNumber =
            "SB" +
            Math.floor(
                10000000 + Math.random() * 90000000
            );


        account = {
            accountNumber: accountNumber,
            name: name,
            phone: phone,
            email: email
        };


        balance = initialDeposit;

        totalDeposit = initialDeposit;


        // Add initial transaction

        transactions.push({
            date: new Date().toLocaleString(),
            type: "Account Opening",
            amount: initialDeposit,
            balance: balance
        });


        updateDashboard();

        alert(
            "Account created successfully!\n\n" +
            "Account Number: " +
            accountNumber
        );


        document
            .getElementById("accountForm")
            .reset();

    });


// ================= DEPOSIT =================

function depositMoney() {

    if (!account) {
        alert("Please create an account first.");
        return;
    }


    const amount =
        Number(
            document.getElementById("depositAmount").value
        );


    if (amount <= 0) {

        alert("Enter a valid amount.");

        return;
    }


    balance += amount;

    totalDeposit += amount;


    transactions.push({

        date: new Date().toLocaleString(),

        type: "Deposit",

        amount: amount,

        balance: balance

    });


    document.getElementById("depositAmount").value = "";


    updateDashboard();

    alert("₹" + amount + " deposited successfully.");

}


// ================= WITHDRAW =================

function withdrawMoney() {

    if (!account) {

        alert("Please create an account first.");

        return;
    }


    const amount =
        Number(
            document.getElementById("withdrawAmount").value
        );


    if (amount <= 0) {

        alert("Enter a valid amount.");

        return;
    }


    // Check balance

    if (amount > balance) {

        alert("Insufficient balance.");

        return;
    }


    // Check daily limit

    if (dailyWithdraw + amount > DAILY_LIMIT) {

        alert(
            "Daily withdrawal limit exceeded.\n" +
            "Your limit is ₹20,000."
        );

        return;
    }


    balance -= amount;

    totalWithdraw += amount;

    dailyWithdraw += amount;


    transactions.push({

        date: new Date().toLocaleString(),

        type: "Withdrawal",

        amount: amount,

        balance: balance

    });


    document.getElementById("withdrawAmount").value = "";


    updateDashboard();

    alert("₹" + amount + " withdrawn successfully.");

}


// ================= CHECK BALANCE =================

function checkBalance() {

    if (!account) {

        alert("Please create an account first.");

        return;
    }


    document.getElementById("balanceMessage").innerHTML =
        "<strong>Current Balance: ₹" +
        balance.toFixed(2) +
        "</strong>";
}


// ================= ACCOUNT INFORMATION =================

function showAccountInfo() {

    if (!account) {

        alert("Please create an account first.");

        return;
    }


    document
        .getElementById("accountInfo")
        .classList.remove("hidden");


    document.getElementById("infoAccount").innerText =
        account.accountNumber;

    document.getElementById("infoName").innerText =
        account.name;

    document.getElementById("infoPhone").innerText =
        account.phone;

    document.getElementById("infoEmail").innerText =
        account.email;

    document.getElementById("infoBalance").innerText =
        "₹" + balance.toFixed(2);


    document
        .getElementById("accountInfo")
        .scrollIntoView({
            behavior: "smooth"
        });
}


// ================= UPDATE DASHBOARD =================

function updateDashboard() {

    document.getElementById("balance").innerText =
        "₹" + balance.toFixed(2);


    document.getElementById("heroBalance").innerText =
        "₹" + balance.toFixed(2);


    document.getElementById("totalDeposit").innerText =
        "₹" + totalDeposit.toFixed(2);


    document.getElementById("totalWithdraw").innerText =
        "₹" + totalWithdraw.toFixed(2);


    document.getElementById("transactionCount").innerText =
        transactions.length;


    if (account) {

        document.getElementById("heroName").innerText =
            account.name;

    }


    // Daily withdrawal

    document.getElementById("dailyWithdraw").innerText =
        "₹" + dailyWithdraw.toFixed(2);


    let percentage =
        (dailyWithdraw / DAILY_LIMIT) * 100;


    document.getElementById("progressBar").style.width =
        percentage + "%";


    updatePassbook();

}


// ================= PASSBOOK =================

function updatePassbook() {

    const table =
        document.getElementById("transactionTable");


    table.innerHTML = "";


    if (transactions.length === 0) {

        table.innerHTML = `
            <tr>
                <td colspan="4">
                    No transactions yet
                </td>
            </tr>
        `;

        return;
    }


    transactions.forEach(function(transaction) {

        const row =
            document.createElement("tr");


        let className = "";

        if (transaction.type === "Deposit") {

            className = "deposit";

        }
        else if (transaction.type === "Withdrawal") {

            className = "withdraw";

        }


        row.innerHTML = `

            <td>${transaction.date}</td>

            <td class="${className}">
                ${transaction.type}
            </td>

            <td class="${className}">
                ₹${transaction.amount.toFixed(2)}
            </td>

            <td>
                ₹${transaction.balance.toFixed(2)}
            </td>

        `;


        table.appendChild(row);

    });

}


// ================= NAVIGATION =================

function showSection(id) {

    document
        .getElementById(id)
        .scrollIntoView({
            behavior: "smooth"
        });

}