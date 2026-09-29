const AUTH_URL = '/api/auth';

async function lireResponse(response){
    if (!response.ok) {
        const error = new Error(
        response.status === 401
            ? 'Identifiant, mot de passe ou session invalide.'
            : response.status === 403
            ? 'Accès refusé.'
            : `Erreur serveur (${response.status}).`
        );

        error.status = response.status;
        throw error;
    }

    if(response.status === 204){
        return null;
    }

    return response.json();
};

export async function login(identifiant, motDePasse){
    const response = await fetch(`${AUTH_URL}/login`,{
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            identifiant,
            motDePasse
        })
    });

    return lireResponse(response);
};

export async function me(token){
    const response = await fetch(`${AUTH_URL}/me`,{
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    return lireResponse(response);
}

export async function logout(token) {
    const response = await fetch(`${AUTH_URL}/logout`, {
        method: 'POST',
        headers: {
        Authorization: `Bearer ${token}`
        }
    });

    return lireReponse(response);
}