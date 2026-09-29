const URL = '/api/utilisateurs';

async function verifier(response) {
    if(!response.ok){
        const error = new Error(
            response.status === 403
                ? 'Vous ne disposez pas des droits nécessaires.'
                : response.status === 401
                    ? 'Session expirée ou révoquée. Veuillez vous reconnecter.'
                    : response.status === 409
                        ? 'Cet identifiant est déjà utilisé.'
                        : `La requête a échoué (${response.status}).`
        );

        error.status = response.status;
        throw error;
    }

    return response.json();
};

export async function listerUtilisateurs(token){
    const response = await fetch(URL,{
        headers:{
            Authorization: `Bearer ${token}`
        }
    });

    return verifier(response);
};

export async function creerUtilisateur(token, {identifiant,motDePasse,roleCode}){
    const response = await fetch(URL,{
        method: 'POST',
        headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            identifiant,
            motDePasse,
            roleCode
        })
    });

    return verifier(response);
}