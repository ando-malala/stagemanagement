export async function getTypeAlerte() {
    const response = await fetch('/api/types-alerte', {
        method: "GET"
    });

    return await response.json();
}