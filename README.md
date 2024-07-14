# Training a model and converting to TensorFlow Lite model
## Objective
The objective was to analyze accelerometer data to detect activities such as walking and standing using a machine learning model and convert the trained model to TensorFlow Lite (.tflite) format for compatibility with an Android project. Finally,
the model should be able to run on a **Stand-alone TensorFlow Lite runtime environment**, allowing the model to run on devices without the support for Google Play Services. 

## Process
### 1. Data Preparation:

Loaded and concatenated multiple CSV files containing accelerometer data.
Extracted relevant features and labels, and encoded the activity labels into numerical values.
Split the data into training and testing sets.
Data set: [Physical Activity Recognition Dataset Using Smartphone Sensors](https://www.utwente.nl/en/eemcs/ps/dataset-folder/sensors-activity-recognition-dataset-shoaib.rar)

### 2. Model Training:

Converted the dataset into time series sequences using TimeseriesGenerator.
Built and compiled a Long Short-Term Memory (LSTM) neural network model with appropriate layers for time series data. [^1]
Trained the model using the training data and validated it with the test data.
[^1]: Credits:
  https://github.com/developershutt/Human-Activity-Recognition-on-Android/tree/master
### 3. Exporting the Model:

Saved the trained Keras model and exported it to TensorFlow's .pb format.
Converted the model variables to constants, effectively freezing the graph.
Saved the model in TensorFlow SavedModel format.
### 4. Converting to TensorFlow Lite:

Used [TensorFlow Lite Converter](https://www.tensorflow.org/lite/models/convert/convert_models) to convert the SavedModel to a .tflite model.
Saved the .tflite model for deployment in an Android project.

> [!TIP]
> Further quantization can be done to reduce model size while also improving CPU and hardware accelerator latency, with little degradation in model accuracy. [Post-training quantization](https://www.tensorflow.org/lite/performance/post_training_quantization)

This process ensures the trained model is optimized for efficient inference on mobile and embedded devices.

**TensorFlow versions used**

```
implementation("org.tensorflow:tensorflow-lite:2.6.0")
implementation("org.tensorflow:tensorflow-lite-select-tf-ops:2.6.0")
```
