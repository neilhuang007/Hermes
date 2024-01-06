// modules.js

console.log('modules.js loaded');

// Fetch and add modules based on the API response
async function loadModules(category) {
    try {
        console.log(`Fetching modules for category: ${category}`);
        const response = await fetch(`http://localhost:1342/api/modulesList?category=${category}`);
        const modulesData = await response.json();

        // Assuming modulesData is an object with module names as keys
        Object.keys(modulesData).forEach(moduleName => {
            const module = modulesData[moduleName];
            addModule(module);
        });
    } catch (error) {
        console.error('Error fetching modules:', error);
    }
}

// Function to add a single module to the page
function addModule(module) {
    const moduleContainer = document.querySelector('.module_container');

    const moduleElement = document.createElement('div');
    moduleElement.classList.add('module');

    const nameElement = document.createElement('div');
    nameElement.classList.add('name');

    const h2Element = document.createElement('h2');
    h2Element.textContent = module.name;

    const descriptionElement = document.createElement('div');
    descriptionElement.classList.add('description');
    descriptionElement.textContent = module.description;

    // Add more elements for other properties

    nameElement.appendChild(h2Element);
    nameElement.appendChild(descriptionElement);
    moduleElement.appendChild(nameElement);
    moduleContainer.appendChild(moduleElement);

    console.log(`Module added: ${module.name}`);
}

// Call the function with the desired category
loadModules('combat'); // Replace 'combat' with the actual category you want
