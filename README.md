# frapuse - AI Companion Android App

This is frapuse (short for frappante muse) an android app project that I created as my final assignment for the Module 3 - Android App Development course at Syntax Institut.

## Features

- The app is a mobile user interface for the text-generation-webui, where you can chat with a localy hosted llm.
- The app is a minimal user interface for the stable-diffusion-webui. You can either use it through the keyword ```generate``` followed with the desired prompt for image generation or use it directly from a dedicated fragment.
- The app allows the upload of PDFs which are localy stored with elasticsearch and accessed through an haystack pipeline. In order to activate the extension you need to activate the checkbox.

- The app uses Kotlin as the programming language and follows the MVVM architecture pattern.

## Installation

1. Prerequisites
    - In order to use the text generation capabilities you need to install [oobabooga/text-generation-webui](https://github.com/oobabooga/text-generation-webui).
      - [Installation](https://github.com/oobabooga/text-generation-webui#installation)

    - For the use of image generation you also need to install [AUTOMATIC1111/stable-diffusion-webui](https://github.com/AUTOMATIC1111/stable-diffusion-webui).
      - [Installation and Running](https://github.com/AUTOMATIC1111/stable-diffusion-webui#installation-and-running)

    - If you want to extract information of PDFs and chat about the context with your llm you also need to install [deepset-ai/haystack](https://github.com/deepset-ai/haystack/).
      (*Note: In order to use this feature you probably are going to need to adjust the pipeline. With no previous experience it can be quite tidious.*)
      - [Installation](https://github.com/deepset-ai/haystack#-installation)
      - [Rest API Setup Documentation](https://docs.haystack.deepset.ai/docs/rest_api#setting-up-a-rest-api-with-haystack)
     
3. To run the app, you need to have Android Studio installed on your computer.
4. Clone this repository and open it in Android Studio.
5. Place the links for each api inside the according file.
    
    - text-generation-webui api
      [Blocking](https://github.com/frapastique/frapuse-ai-companion-android-app/blob/master/app/src/main/java/com/back/frapuse/data/textgen/remote/TextGenBlockApiService.kt#L16)
      /
      [Stream](https://github.com/frapastique/frapuse-ai-companion-android-app/blob/master/app/src/main/java/com/back/frapuse/data/textgen/remote/TextGenStreamWebSocketClient.kt#L13)
     
    - [stable-diffusion-webui api](https://github.com/frapastique/frapuse-ai-companion-android-app/blob/master/app/src/main/java/com/back/frapuse/data/imagegen/remote/ImageGenAPIService.kt#L19)

    - [haystack api](https://github.com/frapastique/frapuse-ai-companion-android-app/blob/master/app/src/main/java/com/back/frapuse/data/textgen/remote/TextGenHaystackApiService.kt#L18)
    
6. Run App on your Android Phone.

## Limitations / Caveats / Known Issues

- *IMPORTANT*: To clear the chat history you have to long press the send button in chat next to the prompt text field.
- Sometimes, when the phone is under heavy load, some of the first tokens are omitted.
- The prompt template is adjusted to [vicuna](https://huggingface.co/TheBloke/Wizard-Vicuna-7B-Uncensored-GPTQ#prompt-template-vicuna) (most of the testing was performed with wizard-vicuna and its varying models), changing the template is a tidious task and also has to be done directly from inside the code. Note that other models still can be used but wont perform at their best.
- Generation parameters of llm must be adjusted inside of the code
  ([llm chat](https://github.com/frapastique/frapuse-ai-companion-android-app/blob/master/app/src/main/java/com/back/frapuse/ui/textgen/TextGenViewModel.kt#L512),
  [llm image](https://github.com/frapastique/frapuse-ai-companion-android-app/blob/master/app/src/main/java/com/back/frapuse/ui/textgen/TextGenViewModel.kt#L1255),
  [llm haystack](https://github.com/frapastique/frapuse-ai-companion-android-app/blob/master/app/src/main/java/com/back/frapuse/ui/textgen/TextGenViewModel.kt#L1079)).
- Currently it is only possible to stream the response (*Note*: You still need to adjust the blocking api address else the app crashes).
- The prompt examples for image generation within the chat must be adjusted inside of the [code](https://github.com/frapastique/frapuse-ai-companion-android-app/blob/master/app/src/main/java/com/back/frapuse/ui/textgen/TextGenViewModel.kt#L1175).
- To disable calling image generation with the keyword ```generate``` within the chat you have to change the [code](https://github.com/frapastique/frapuse-ai-companion-android-app/blob/master/app/src/main/java/com/back/frapuse/ui/textgen/TextGenViewModel.kt#L328).
- It is not possible to save generated images larger than the size of 768x768. If the width OR height is adjusted beyond this value, the other must be adjusted accordingly.
- The code is not optimized and at parts it can be a bit messy. Please excuse this, I am still learning and improoving my code style and habbits.

## App Previews

### Screenshots:

<table>
  <tr align="center">
    <td>
      <p>Home Screen</p>
      <img
        src="https://github.com/frapastique/frapuse-ai-companion-android-app/assets/66075561/f313a61e-7284-433a-8b46-e4f9cc11eab2"
        alt="Home Screen"
        width="75%"
        height="75%"
        title="Home Screen"
      />
    </td>
    <td>
      <p>Chat Screen Base</p>
      <img
        src="https://github.com/frapastique/frapuse-ai-companion-android-app/assets/66075561/c441cabb-0783-4efb-b581-932d0af7daad"
        alt="Chat Screen Base"
        width="75%"
        height="75%"
        title="Chat Screen Base"
      />
    </td>
  </tr>
  <tr align="center">
    <td>
      <p>Chat Screen Image Gen</p>
      <img
        src="https://github.com/frapastique/frapuse-ai-companion-android-app/assets/66075561/71554819-2127-4af4-9b42-8091748d9320"
        alt="Chat Screen Image Gen"
        width="75%"
        height="75%"
        title="Chat Screen Image Gen"
      />
    </td>
    <td>
      <p>PDF Upload Screen</p>
      <img
        src="https://github.com/frapastique/frapuse-ai-companion-android-app/assets/66075561/cbddef0c-f9d8-4bff-b557-49c6f4e72bd3"
        alt="PDF Upload Screen"
        width="75%"
        height="75%"
        title="PDF Upload Screen"
      />
    </td>
  </tr>
  <tr align="center">
    <td>
      <p>Chat Settings Screen</p>
      <img
        src="https://github.com/frapastique/frapuse-ai-companion-android-app/assets/66075561/c47a56bb-70ba-471c-bc95-80f536ffa7d4"
        alt="Chat Settings Screen"
        width="75%"
        height="75%"
        title="Chat Settings Screen"
      />
    </td>
    <td>
      <p>Chat Screen Document Search</p>
      <img
        src="https://github.com/frapastique/frapuse-ai-companion-android-app/assets/66075561/cd9ff384-d097-408b-b597-c0934245b249"
        alt="Chat Screen Document Search"
        width="75%"
        height="75%"
        title="Chat Screen Document Search"
      />
    </td>
  </tr>
  <tr align="center">
    <td>
      <p>Document View Screen</p>
      <img
        src="https://github.com/frapastique/frapuse-ai-companion-android-app/assets/66075561/054167f4-7812-4e13-ac27-f00c3e235eb6"
        alt="Document View Screen"
        width="75%"
        height="75%"
        title="Document View Screen"
      />
    </td>
    <td>
      <p>Image Generation Screen</p>
      <img
        src="https://github.com/frapastique/frapuse-ai-companion-android-app/assets/66075561/c8f39713-7aed-4385-8b36-2334ff8dabe9"
        alt="Image Generation Screen"
        width="75%"
        height="75%"
        title="Image Generation Screen"
      />
    </td>
  </tr>
  <tr align="center">
    <td>
      <p>Image Library Small Screen</p>
      <img
        src="https://github.com/frapastique/frapuse-ai-companion-android-app/assets/66075561/3e7e8a90-dd64-4299-834e-2401a851bc84"
        alt="Image Library Small Screen"
        width="75%"
        height="75%"
        title="Image Library Small Screen"
      />
    </td>
    <td>
      <p>Image Library Big Screen</p>
      <img
        src="https://github.com/frapastique/frapuse-ai-companion-android-app/assets/66075561/719ef542-2722-47b7-a94a-9e30cdccb96e"
        alt="Image Library Big Screen"
        width="75%"
        height="75%"
        title="Image Library Big Screen"
      />
    </td>
  </tr>
</table>

### Video:
[YouTube Link](https://www.youtube.com/shorts/Z3cNasTxsbY)

##
### Special thanks to Syntax Institute and the devs of the in this project used dependecies!
##

#### Feel free to provide feedback and ask questions. I'm happy to help and willing to improve my work! :)
