# README #

### Bitbucket Setup Instructions for Android Studio ###

1. Open Android Studio
2. From the start window, choose "Checkout Project from Version control"
3. Select the "Git" option from the dropdown menu
4. Enter "https://<your_username>@bitbucket.org/pico_app/pico.git" in the "Git Repository URL" field
5. Click the "Clone" button
6. When prompted to open the project, hit "Yes"

**NOTE:** You may receive errors at this stage -- this is normal. If the error message is due to the "*.iml" files, choose the option to remove all, then rebuild. If you receive any SDK errors, follow the prompts from Android Studio to resolve them. If you receive any other errors, good luck.

Once this is finished, the project should have been successfully imported.

### Additional Notes: ###

Since there's a bunch of us working on the same thing, make sure to create your own branch and commit all your changes there to avoid us accidentally creating conflicts. When you're finished, create a pull request and we can resolve any potential merge conflicts there.

#### Make sure to reference the following link when creating any UI elements: #### https://www.luminpdf.com/viewer/RCHKHq4EHJcj3Rvdp/share?sk=945fe795-bfd4-47d5-81e8-3fd3ab4ad039