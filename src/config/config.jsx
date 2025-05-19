const api = {
    baseURL: "https://warthog-subtle-dodo.ngrok-free.app/api",

    async request(url, options = {}) {
        const token = window.localStorage.getItem("persist:auth")
            ? JSON.parse(window.localStorage.getItem("persist:auth")).token
            : null;

        if (token) {
            console.log("Sending request with token:", token); // Debug token
            options.headers = {
                ...options.headers,
                authorization: `Bearer ${token}`,
            };
        }

        try {
            const response = await fetch(`${this.baseURL}${url}`, options);

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'An error occurred');
            }

            const data = await response.json();
            return data;
        } catch (error) {
            console.error("Request error:", error);
            return { error: error.message };
        }
    },

    async post(url, body, options = {}) {
        return this.request(url, {
            ...options,
            method: 'POST',
            body: JSON.stringify(body),
            headers: {
                ...options.headers,
                'Content-Type': 'application/json',
            },
        });
    },
};

export default api;