async function signUp(){

    const btn = document.getElementById("signUpBtn");
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Creating Account...';

    let firstName = document.getElementById("firstName");
    let lastName = document.getElementById("lastName");
    let email = document.getElementById("email");
    let password = document.getElementById("password");

    const user = {
        firstName: firstName.value,
        lastName: lastName.value,
        email: email.value,
        password: password.value
    }

    try{
        const response = await fetch("api/users/register",{
            method:"POST",
            headers:{
                "Content-Type":"application/json"
            },
            body:JSON.stringify(user)
        });

        if(response.ok){
            const data = await response.json();
            if(data.success){
                localStorage.setItem("user", JSON.stringify({
                    username: firstName.value
                }));
                iziToast.success(
                    {
                        timeout:800,
                        message:data.message,
                        position:'topRight',
                        onClosing: ()=>{
                            window.location = "index.html"
                        }
                    }
                );
            }else{
                iziToast.error(
                    {
                        timeout:3000,
                        message:data.message,
                        position:'topRight',
                    }
                );
            }
        }else{
            iziToast.error(
                {
                    timeout:3000,
                    message:"Something went wrong, please try again later.",
                    position:'topRight',
                }
            );
        }
    }catch (e) {
        iziToast.error(
            {
                timeout:3000,
                message:e.message,
                position:'topRight',
            }
        );
    }finally {
        btn.disabled = false;
        btn.innerHTML = 'Create Account';
    }
}