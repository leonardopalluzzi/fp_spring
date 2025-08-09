const addUserBtn = document.getElementById('addUserBtn');
const container = document.getElementById('user-container');

let users = [
    {
        username: '',
        email: '',
        password: ''
    }
];


function renderUserInputGroup(index, username, email, password) {
    return `
        <div class="input-container row row-cols-3" data-index="${index}">
            <div class="mb-3 col">
                <label class="form-label">Username</label>
                <input name="users[${index}].username" value="${username}" type="text" class="form-control" placeholder="mariorossi" required />
            </div>

            <div class="mb-3 col">
                <label class="form-label">Email</label>
                <input name="users[${index}].email" value="${email}" type="email" class="form-control" placeholder="mariorossi@domain.com" required />
            </div>

            <div class="mb-3 col">
                <label class="form-label">Password</label>
                <input name="users[${index}].password" value="${password}" type="password" class="form-control" placeholder="passSicura!.123" required />
            </div>

            <div class="w-100 text-center">
                <button type="button" class="delete-input-btn btn btn-danger"><i class="bi bi-file-minus"></i></button>
            </div>
        </div>
    `;
}


function renderForm() {
    container.innerHTML = '';
    users.forEach((item, i) => {
        container.insertAdjacentHTML("beforeend", renderUserInputGroup(i, item.username, item.email, item.password))
    })
}

renderForm();


addUserBtn.addEventListener('click', () => {
    if (users.length == 5) {
        return
    }
    users.push({ username: '', email: '', password: '' });

    renderForm();
})

container.addEventListener('click', (e) => {
    if (users.length == 1) {
        return
    }
    if (e.target.classList.contains('delete-input-btn')) {
        const containerDiv = e.target.closest('.input-container');
        const indexToDelete = parseInt(containerDiv.dataset.index);
        users.splice(indexToDelete, 1);
        renderForm();
    }
});