async function signIn() {

    let email = document.getElementById("email");
    let password = document.getElementById("password");

    const userLoginObj = {
        email: email.value,
        password: password.value
    }

    try {
        const response = await fetch("api/users/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(userLoginObj)
        });


        if (response.ok) {
            const data = await response.json();
            if(data.success){
                localStorage.setItem("user", JSON.stringify({
                    username: data.data?.firstName || email.value.split('@')[0]
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
                        message:data.message,
                        position:'topRight',
                    }
                );
            }

        } else {
            iziToast.warning(
                {
                    message:"Something went wrong. Please try again later.",
                    position:'center',
                }
            );
        }
    } catch (e) {
        console.log(e.message);
    }
}