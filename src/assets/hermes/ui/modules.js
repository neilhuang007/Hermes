console.log('modules.js loaded');

// Function to add a single module to the page

function addModule(module) {
    const moduleContainer = document.querySelector('.module_container');

    const moduleElement = document.createElement('div');
    moduleElement.classList.add('module');

    const moduleContent = document.createElement('div');
    moduleContent.classList.add('module_content');

    const titleElement = document.createElement('h2');
    titleElement.textContent = module.name;
    titleElement.addEventListener('click', function() {
        toggleModuleState(module.name, module.enabled);
    });
    moduleContent.appendChild(titleElement);

    const descriptionElement = document.createElement('p');
    descriptionElement.textContent = module.description;
    moduleContent.appendChild(descriptionElement);

    // Create the settings container for this module
    const settingsContainer = document.createElement('div');
    settingsContainer.className = 'settings-container';
    settingsContainer.style.display = 'none'; // Initially hidden
    moduleContent.appendChild(settingsContainer);

    // Check if the module is enabled and update UI
    if (module.enabled) {
        updateModuleUI(moduleElement, module.name, true);
    }

    // Create a toggle button (initially 'Settings')
    const toggleButton = document.createElement('a');
    toggleButton.setAttribute('href', '#');
    toggleButton.textContent = 'Settings';
    toggleButton.addEventListener('click', function(event) {
        event.preventDefault();
        toggleModuleSettings(settingsContainer, module, titleElement, descriptionElement, toggleButton);
    });

    // Create a toggle button (initially 'Settings')
    const ModuletoggleButton = document.createElement('a');
    ModuletoggleButton.setAttribute('href', '#');
    if(module.enabled){
        ModuletoggleButton.textContent = 'UnToggle';
    }else{
        ModuletoggleButton.textContent = 'Toggle';
    }

    ModuletoggleButton.style.marginLeft = '10px';
    ModuletoggleButton.addEventListener('click', function(event) {
        event.preventDefault();
        if(module.enabled){
            toggleModuleState(module.name,false)
            ModuletoggleButton.textContent = 'Toggle';
        }else{
            toggleModuleState(module.name,true)
            ModuletoggleButton.textContent = 'UnToggle';
        }
    });




    moduleContent.appendChild(toggleButton);
    moduleContent.appendChild(ModuletoggleButton);

    moduleElement.appendChild(moduleContent);
    moduleContainer.appendChild(moduleElement);
}

function toggleModuleSettings(settingsContainer, module, titleElement, descriptionElement, toggleButton) {
    const isSettingsVisible = settingsContainer.style.display !== 'none';

    // Toggle visibility of title, description, and change button text
    titleElement.style.display = isSettingsVisible ? 'block' : 'none';
    descriptionElement.style.display = isSettingsVisible ? 'block' : 'none';
    toggleButton.textContent = isSettingsVisible ? 'Settings' : 'Back';

    // Populate settings if they are about to be shown
    if (!isSettingsVisible) {
        populateSettings(settingsContainer, module);
        loadSettings(module.name);
    }

    // Toggle settings container visibility
    settingsContainer.style.display = isSettingsVisible ? 'none' : 'block';
}

function populateSettings(settingsContainer, module) {
    settingsContainer.innerHTML = ''; // Clear previous content

    // Create settings title
    const settingsTitle = document.createElement('h2');
    settingsTitle.textContent = module.name + ' Settings';
    settingsContainer.appendChild(settingsTitle);

    // TODO: Create and append module-specific settings here
    // Create a new div element (you can customize this div as needed)
    const customDiv = document.createElement('div');
    customDiv.className = "settings_content"
    customDiv.textContent = 'This is a custom div between the module name and the back button.';
    settingsContainer.appendChild(customDiv);
}
// Fetch and add modules based on the API response
async function loadModules(category) {
    try {
        // Clear existing modules
        const moduleContainer = document.querySelector('.module_container');
        moduleContainer.innerHTML = '';

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

async function loadSettings(moduleName) {
    try {
        // Clear existing settings content
        const settingsContainer = document.querySelector('.settings_content');
        settingsContainer.innerHTML = '';

        // Fetch module settings from the API
        const response = await fetch(`http://localhost:1342/api/getModuleSetting?module=${moduleName}`);
        const settingsData = await response.json();

        if (settingsData.success) {
            // Process and display each setting
            settingsData.result.forEach(setting => {
                createSettingElement(setting, settingsContainer,moduleName);
            });
        } else {
            console.error('Error loading settings:', settingsData.reason);
            // Handle error (e.g., display a message to the user)
        }
    } catch (error) {
        console.error('Error fetching settings:', error);
        // Handle network or other error
    }
}

function createBooleanSetting(setting, container, moduleName) {
    const settingElement = document.createElement('div');
    settingElement.className = 'setting';
    settingElement.style.display = 'flex';
    settingElement.style.justifyContent = 'space-between';
    settingElement.style.alignItems = 'center';
    settingElement.style.width = '100%'; // Ensure the setting element takes the full width

    const label = document.createElement('label');
    label.textContent = setting.name;
    label.style.flexGrow = 1; // Allow the label to take up available space

    // Create the toggle switch
    const toggleLabel = document.createElement('label');
    toggleLabel.className = 'toggle-switch';

    const checkbox = document.createElement('input');
    checkbox.setAttribute('type', 'checkbox');
    checkbox.checked = setting.value;

    const toggleBackground = document.createElement('div');
    toggleBackground.className = 'toggle-switch-background';

    const toggleHandle = document.createElement('div');
    toggleHandle.className = 'toggle-switch-handle';

    checkbox.addEventListener('change', () => {
        updateModuleSettings(moduleName, setting.name, checkbox.checked);
    });

    toggleBackground.appendChild(toggleHandle);
    toggleLabel.appendChild(checkbox);
    toggleLabel.appendChild(toggleBackground);

    settingElement.appendChild(label);
    settingElement.appendChild(toggleLabel);
    container.appendChild(settingElement);
}



// ... other functions ...

function createSliderSetting(setting, container, moduleName) {
    let hideIndicatorTimeout;
    const settingElement = document.createElement('div');
    settingElement.className = 'setting';

    const label = document.createElement('label');
    label.textContent = setting.name;
    settingElement.appendChild(label);

    const field = document.createElement('div');
    field.className = 'field';
    settingElement.appendChild(field);

    const minValueLabel = document.createElement('div');
    minValueLabel.className = 'value left';
    minValueLabel.textContent = setting.min;
    field.appendChild(minValueLabel);

    const slider = document.createElement('input');
    slider.type = 'range';
    slider.min = setting.min;
    slider.max = setting.max;
    slider.value = setting.value;
    slider.step = setting.step || 1;
    field.appendChild(slider);

    const progressBar = document.createElement('div');
    progressBar.className = 'progress-bar';
    field.appendChild(progressBar);

    const slideValue = document.createElement('span');
    slideValue.className = 'sliderValue';
    field.appendChild(slideValue);

    const maxValueLabel = document.createElement('div');
    maxValueLabel.className = 'value right';
    maxValueLabel.textContent = setting.max;
    field.appendChild(maxValueLabel);

    const updateProgressBar = () => {
        const percentage = ((slider.value - slider.min) / (slider.max - slider.min)) * 100;

        // Adjust the progress bar's width to align with the center of the thumb
        const thumbOffset = 10; // Half of the thumb's width
        const progressBarWidth = Math.min(percentage, 100) * (slider.offsetWidth - thumbOffset * 2) / 100 + thumbOffset - 5;
        progressBar.style.width = `${progressBarWidth}px`;
        progressBar.style.left = '35px'
    };

    const updateValueIndicator = () => {
        slideValue.textContent = slider.value;
        const percentage = ((slider.value - slider.min) / (slider.max - slider.min)) * 100;

        // Adjust the progress bar's width to align with the center of the thumb
        const thumbOffset = 10; // Half of the thumb's width
        const progressBarWidth = Math.min(percentage, 100) * (slider.offsetWidth - thumbOffset * 2) / 100 + thumbOffset - 7;

        slideValue.style.left = `calc(${progressBarWidth}px + 40px)`;
        slideValue.classList.add("show");
    };

    function hideValueIndicator() {
        slideValue.classList.remove("show");
    }

    slider.oninput = () => {
        updateProgressBar();
        updateValueIndicator();

        clearTimeout(hideIndicatorTimeout);
        hideIndicatorTimeout = setTimeout(hideValueIndicator, 1000); // Hide after 1 second of inactivity
        updateModuleSettings(moduleName, setting.name, slider.value);
    };



    container.appendChild(settingElement);

    updateProgressBar();
    updateValueIndicator();
    hideValueIndicator()
}

// ... other functions ...


function createRangeSliderSetting(setting, container, moduleName) {
    // Create a div that will act as the setting box
    const settingBox = document.createElement('div');
    settingBox.className = 'setting';

    // Create and add label
    const label = document.createElement('label');
    label.textContent = setting.name;
    label.className = 'setting-label'; // Optionally add a class for styling
    settingBox.appendChild(label);

    // Create container for range slider setting
    const rangeContainer = document.createElement('div');
    rangeContainer.className = 'rangevalue_wrapper';
    settingBox.appendChild(rangeContainer);

    // Create inner container
    const innerContainer = document.createElement('div');
    innerContainer.className = 'container';
    rangeContainer.appendChild(innerContainer);

    // Create min value display
    const minValueDisplay = document.createElement('div');
    minValueDisplay.className = 'min-value numberVal';
    const minValueInput = document.createElement('input');
    minValueInput.type = 'number';
    minValueInput.min = setting.min;
    minValueInput.max = setting.max;
    minValueInput.value = setting.minvalue;
    minValueInput.disabled = true;
    minValueDisplay.appendChild(minValueInput);
    innerContainer.appendChild(minValueDisplay);

    // Create range slider
    const rangeSlider = document.createElement('div');
    rangeSlider.className = 'range-slider';
    innerContainer.appendChild(rangeSlider);

    // Create progress bar
    const progressBar = document.createElement('div');
    progressBar.className = 'progress';
    rangeSlider.appendChild(progressBar);

    // Create range inputs
    const minRange = document.createElement('input');
    minRange.type = 'range';
    minRange.className = 'range-min';
    minRange.min = setting.min;
    minRange.max = setting.max;
    minRange.value = setting.minvalue;

    const maxRange = document.createElement('input');
    maxRange.type = 'range';
    maxRange.className = 'range-max';
    maxRange.min = setting.min;
    maxRange.max = setting.max;
    maxRange.value = setting.maxvalue;

    rangeSlider.appendChild(minRange);
    rangeSlider.appendChild(maxRange);

    // Create max value display
    const maxValueDisplay = document.createElement('div');
    maxValueDisplay.className = 'max-value numberVal';
    const maxValueInput = document.createElement('input');
    maxValueInput.type = 'number';
    maxValueInput.min = setting.min;
    maxValueInput.max = setting.max;
    maxValueInput.value = setting.maxvalue;
    maxValueInput.disabled = true;
    maxValueDisplay.appendChild(maxValueInput);
    innerContainer.appendChild(maxValueDisplay);

    // Add event listeners to range inputs
    minRange.addEventListener('input', () => {
        updateRangeSlider(minRange, maxRange, progressBar, minValueInput, maxValueInput);
        // Update module setting with option indicating 'min' value change
        updateModuleSettings(moduleName, setting.name, minRange.value, 'min');
    });

    maxRange.addEventListener('input', () => {
        updateRangeSlider(minRange, maxRange, progressBar, minValueInput, maxValueInput);
        // Update module setting with option indicating 'max' value change
        updateModuleSettings(moduleName, setting.name, maxRange.value,  'max');
    });
    // Initial update of the range slider
    updateRangeSlider(minRange, maxRange, progressBar, minValueInput, maxValueInput);

    // Append the setting box to the main container
    container.appendChild(settingBox);
}


function updateRangeSlider(minInput, maxInput, progress, minValueInput, maxValueInput) {
    console.log("update range slider")
    let minVal = parseFloat(minInput.value);
    let maxVal = parseFloat(maxInput.value);

    if (maxVal < minVal) {
        maxVal = minVal;
        maxInput.value = maxVal;
    }

    let totalRange = parseFloat(maxInput.max) - parseFloat(minInput.min);
    let minPercent = ((minVal - parseFloat(minInput.min)) / totalRange) * 100;
    let maxPercent = ((maxVal - parseFloat(minInput.min)) / totalRange) * 100;

    progress.style.left = minPercent + '%';
    progress.style.right = (100 - maxPercent) + '%';

    console.log("style left:", progress.style.left, "style right:", progress.style.right);

    console.log("Min Percent:", minPercent, "Max Percent:", maxPercent);
    minValueInput.value = minVal;
    maxValueInput.value = maxVal;
}


function createInputSetting(setting, container, moduleName) {
    // Create the wrapper div for the setting
    const settingWrapper = document.createElement('div');
    settingWrapper.className = 'setting';

    // Create the label or name of the setting
    const settingName = document.createElement('div');
    settingName.className = 'setting-name';
    settingName.textContent = setting.name;
    settingWrapper.appendChild(settingName);

    // Create the form control container
    const formControl = document.createElement('div');
    formControl.className = 'form-control';

    // Create and add the input element
    const input = document.createElement('input');
    input.className = 'input';
    input.type = 'text';
    input.placeholder = setting.placeholder || 'Enter text'; // Example placeholder
    input.required = setting.required || false;
    formControl.appendChild(input);

    // Create and add the focus indicator div
    const focusIndicator = document.createElement('div');
    focusIndicator.className = 'input-focus-indicator';

    input.addEventListener('change', () => {
        updateModuleSettings(moduleName, setting.name, input.value);
    });

    formControl.appendChild(focusIndicator);

    // Append the form control to the wrapper
    settingWrapper.appendChild(formControl);

    // Append the setting wrapper to the container
    container.appendChild(settingWrapper);
}

function CreateListSetting(setting, container, moduleName) {
    // Create a div that will act as the setting box
    const settingBox = document.createElement('div');
    settingBox.className = 'setting';

    // Create a label for the setting name
    const settingLabel = document.createElement('div');
    settingLabel.className = 'setting-name';
    settingLabel.textContent = setting.name;
    settingBox.appendChild(settingLabel);

    // Create container for radio buttons
    const radioButtonContainer = document.createElement('div');
    radioButtonContainer.className = 'radio-button-container';

    // Loop through each option in the setting
    setting.values.forEach((option, index) => {
        // Create a div for each radio button
        const radioButtonDiv = document.createElement('div');
        radioButtonDiv.className = 'radio-button';

        // Create the radio input
        const radioButtonInput = document.createElement('input');
        radioButtonInput.type = 'radio';
        radioButtonInput.className = 'radio-button__input';
        radioButtonInput.id = setting.name + index;
        radioButtonInput.name = setting.name;
        radioButtonInput.value = option;
        if (setting.value === option) { // Set the radio button as checked if it matches the setting value
            radioButtonInput.checked = true;
        }

        // Add event listener for radio button change
        radioButtonInput.addEventListener('change', () => {
            updateModuleSettings(moduleName, setting.name, option);
        });

        // Create the label for the radio button
        const radioButtonLabel = document.createElement('label');
        radioButtonLabel.className = 'radio-button__label';
        radioButtonLabel.htmlFor = setting.name + index;

        // Create the custom radio button
        const radioButtonCustom = document.createElement('span');
        radioButtonCustom.className = 'radio-button__custom';

        // Append elements to the radio button div
        radioButtonLabel.appendChild(radioButtonCustom);
        radioButtonLabel.appendChild(document.createTextNode(option));
        radioButtonDiv.appendChild(radioButtonInput);
        radioButtonDiv.appendChild(radioButtonLabel);

        // Append the radio button div to the container
        radioButtonContainer.appendChild(radioButtonDiv);
    });

    // Append the radio button container to the setting box
    settingBox.appendChild(radioButtonContainer);

    // Append the setting box to the main container
    container.appendChild(settingBox);
}

function createModeSetting(setting, container, moduleName) {
    const settingElement = document.createElement('div');
    settingElement.className = 'setting mode-setting';

    const label = document.createElement('label');
    label.className = 'mode-name';
    label.textContent = setting.name;
    settingElement.appendChild(label);

    const modeControl = document.createElement('div');
    modeControl.className = 'mode-control';

    const leftArrow = document.createElement('div');
    leftArrow.className = 'arrow';
    leftArrow.textContent = '<';
    modeControl.appendChild(leftArrow);

    const modeValue = document.createElement('div');
    modeValue.className = 'mode-value';
    modeValue.textContent = setting.value;
    modeControl.appendChild(modeValue);

    const rightArrow = document.createElement('div');
    rightArrow.className = 'arrow';
    rightArrow.textContent = '>';
    modeControl.appendChild(rightArrow);

    settingElement.appendChild(modeControl);
    container.appendChild(settingElement);

    let currentIndex = setting.values.indexOf(setting.value);

    const updateModeValue = (direction) => {
        if (direction === 'left') {
            currentIndex = (currentIndex - 1 + setting.values.length) % setting.values.length;
        } else {
            currentIndex = (currentIndex + 1) % setting.values.length;
        }
        modeValue.textContent = setting.values[currentIndex];
        updateModuleSettings(moduleName, setting.name, setting.values[currentIndex]);
    };

    leftArrow.addEventListener('click', () => updateModeValue('left'));
    rightArrow.addEventListener('click', () => updateModeValue('right'));
}



function createSettingElement(setting, container, moduleName) {
    switch (setting.type) {
        case 'checkbox':
            createBooleanSetting(setting, container, moduleName);
            break;
        case 'slider':
            createSliderSetting(setting, container, moduleName);
            break;
        case 'range_slider':
            createRangeSliderSetting(setting, container, moduleName);
            break;
        case 'input':
            createInputSetting(setting, container, moduleName);
            break;
        case 'radio':
            CreateListSetting(setting, container, moduleName);
            break;
        // Add cases for other types as needed
        case 'mode':
            createModeSetting(setting, container, moduleName);
            break;
    }
}


// Event listener for category buttons
document.addEventListener('DOMContentLoaded', function () {
    const categoryButtons = document.querySelectorAll('.catagory_name');
    categoryButtons.forEach(button => {
        button.addEventListener('click', function () {
            const category = this.textContent; // Assuming the category name is the same as expected by the API
            loadModules(category);
        });
    });
    // Initial load with default category
    loadModules('Render');
});

async function updateModuleSettings(moduleName, settingName, settingValue, options = "none") {
    // Construct the URL with query parameters
    let url = `http://localhost:1342/api/setModuleSettingValue?module=${encodeURIComponent(moduleName)}&name=${encodeURIComponent(settingName)}&value=${encodeURIComponent(settingValue)}&options=${encodeURIComponent(options)}`;

    try {
        const response = await fetch(url);
        const data = await response.json();

        if (data.success) {
            console.log('Setting updated successfully:', data.result);
            // Perform any additional actions based on the response
        } else {
            console.error('Error updating setting:', data.reason);
            alert(data.reason);
        }
    } catch (error) {
        console.error('Error in updateModuleSettings:', error);
        alert("Error: " + error);
    }
}

// Example usage (assuming you have a specific setting and module name)
// updateModuleSettings('ModuleName', 'SettingName', 'NewValue', 'SettingType', { optionKey: 'optionValue' });


async function toggleModuleState(moduleName, isEnabled) {
    const url = `http://localhost:1342/api/updateModulesInfo?displayname=${encodeURIComponent(moduleName)}&enable=${!isEnabled}`;

    try {
        const response = await fetch(url);
        const data = await response.json();
        if (data.success) {
            console.log('Module state updated successfully');
            return !isEnabled; // Return the new state
        } else {
            console.error('Error updating module state:', data.reason);
        }
    } catch (error) {
        console.error('Error in toggleModuleState:', error);
    }
}

function updateModuleUI(moduleElement, moduleName, isEnabled) {
    const titleElement = document.querySelector('.module_content h2');

    console.log("aa")
    if (isEnabled) {
        titleElement.style.content += '(enabled)';
    } else {
        titleElement.style.color = 'White';
    }
}


document.querySelectorAll('.module').forEach(moduleElement => {
    const moduleName = moduleElement.dataset.moduleName; // Assuming module name is stored in data attribute
    let isEnabled = moduleElement.dataset.isEnabled === 'true'; // Convert string to boolean

    moduleElement.querySelector('h2').addEventListener('click', async () => {
        isEnabled = await toggleModuleState(moduleName, isEnabled); // Toggle state and get new state
        updateModuleUI(moduleElement, moduleName, isEnabled); // Update UI
        moduleElement.dataset.isEnabled = isEnabled; // Update data attribute
    });
});
