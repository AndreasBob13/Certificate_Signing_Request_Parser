document.addEventListener("DOMContentLoaded", () => {
    // Get references to DOM elements
    const fileInput = document.getElementById("fileInput");
    const uploadButton = document.getElementById("uploadButton");
    const message = document.getElementById("message");

    uploadButton.addEventListener("click", async () => {
        const files = fileInput.files;

        // Check if a file is selected
        if (files.length === 0) {
            message.innerText = "Please select a file!";
            return;
        }

        // Prepare the FormData object with the selected file
        const formData = new FormData();
        formData.append("csr", files[0]); // Add only the first file

        const fetchData = async () => {
            try {
                // Send the file to the server using the POST method
                const response = await fetch("/api/parse-csr", {
                    method: "POST",
                    body: formData,
                });

                // Handle server response
                if (!response.ok) {
                    throw new Error(`Error: ${response.statusText}`);
                }

                const result = await response.json();
                console.log(result);

                // Get references to the output elements
                const subjectElement = document.getElementById("subject");
                const publicKeyAlgorithmElement = document.getElementById("publicKeyAlgorithm");
                const subjectAltNameElement = document.getElementById("subjectAltName");

                // Display the 'subject' data
                if (subjectElement) {
                    subjectElement.innerText = result.subject.join(", ");
                } else {
                    console.error("Element with ID 'subject' not found");
                }

                // Display the 'publicKeyAlgorithm' data
                if (publicKeyAlgorithmElement) {
                    publicKeyAlgorithmElement.innerText = result.publicKeyAlgorithm;
                } else {
                    console.error("Element with ID 'publicKeyAlgorithm' not found");
                }

                // Display the 'subjectAltName' data
                if (subjectAltNameElement) {
                    subjectAltNameElement.innerText = result.subjectAltName.join(", ");
                } else {
                    console.error("Element with ID 'subjectAltName' not found");
                }

                // Display success message
                message.innerText = "File uploaded successfully!";
            } catch (error) {
                // Handle errors during the upload process
                console.error("File upload failed:", error);
                message.innerText = "Error uploading the file.";
            }
        };

        fetchData(); // Execute the fetch operation
    });
});
