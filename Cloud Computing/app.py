import base64
import numpy as np
import tensorflow as tf
from io import BytesIO
from matplotlib import pyplot as plt
from PIL import Image

from object_detection.utils import ops as utils_ops
from object_detection.utils import label_map_util
from object_detection.utils import visualization_utils as vis_util

# patch tf1 into `utils.ops`
utils_ops.tf = tf.compat.v1

# Patch the location of gfile
tf.gfile = tf.io.gfile

PATH_TO_LABELS = 'inference_graph/saved_model.pbtxt' #Disesuaikan dengan tempat label_map.pbtxt
category_index = label_map_util.create_category_index_from_labelmap(PATH_TO_LABELS, use_display_name=True)

detection_model = tf.saved_model.load("inference_graph/saved_model") #Disesuaikan dengan /inference_graph/saved_model

def run_inference_for_single_image(model, image):
  image = np.asarray(image)
  # The input needs to be a tensor, convert it using `tf.convert_to_tensor`.
  input_tensor = tf.convert_to_tensor(image)
  # The model expects a batch of images, so add an axis with `tf.newaxis`.
  input_tensor = input_tensor[tf.newaxis,...]

  # Run inference
  model_fn = model.signatures['serving_default']
  output_dict = model_fn(input_tensor)

  # All outputs are batches tensors.
  # Convert to numpy arrays, and take index [0] to remove the batch dimension.
  # We're only interested in the first num_detections.
  num_detections = int(output_dict.pop('num_detections'))
  output_dict = {key:value[0, :num_detections].numpy() 
                 for key,value in output_dict.items()}
  output_dict['num_detections'] = num_detections

  # detection_classes should be ints.
  output_dict['detection_classes'] = output_dict['detection_classes'].astype(np.int64)
   
  # Handle models with masks:
  if 'detection_masks' in output_dict:
    # Reframe the the bbox mask to the image size.
    detection_masks_reframed = utils_ops.reframe_box_masks_to_image_masks(
              output_dict['detection_masks'], output_dict['detection_boxes'],
               image.shape[0], image.shape[1])      
    detection_masks_reframed = tf.cast(detection_masks_reframed > 0.5,
                                       tf.uint8)
    output_dict['detection_masks_reframed'] = detection_masks_reframed.numpy()
    
  return output_dict

# Custom Stephen
def show_inference(model, image_path):
  # the array based representation of the image will be used later in order to prepare the
  # result image with boxes and labels on it.
  image_np = np.array(image_path)
  # Actual detection.
  output_dict = run_inference_for_single_image(model, image_np)

  terdeteksi = []

  for i in range(len(output_dict['detection_classes'])):
    if output_dict['detection_scores'][i] >= 0.5:
      if category_index[output_dict['detection_classes'][i]]['name'] not in terdeteksi:
        terdeteksi.append(category_index[output_dict['detection_classes'][i]]['name'])
      print('Kelas Terdeteksi : ', category_index[output_dict['detection_classes'][i]]['name'], output_dict['detection_scores'][i])  

  # Visualization of the results of a detection.
  vis_util.visualize_boxes_and_labels_on_image_array(
      image_np,
      output_dict['detection_boxes'],
      output_dict['detection_classes'],
      output_dict['detection_scores'],
      category_index,
      instance_masks=output_dict.get('detection_masks_reframed', None),
      use_normalized_coordinates=True,
      line_thickness=8)

  img = Image.fromarray(image_np)                  
  buffer = BytesIO()
  img.save(buffer,format="JPEG")                  
  the_image = buffer.getvalue()
  image_base64= base64.encodebytes(the_image).decode('utf-8')
  hasil={
    "terdeteksi" : terdeteksi,
    "image_base64" : image_base64
  }
  return hasil
  
from flask import Flask, request, jsonify
app = Flask(__name__)
@app.route('/feedeep/', methods=['GET', 'POST'])
def welcome():
  if request.method=='GET':
    return 'Using POST'
  if request.method=='POST':
    image_pro=request.get_json()
    im = Image.open(BytesIO(base64.b64decode(image_pro['base64'])))
    return jsonify(show_inference(detection_model, im))
if __name__ == '__main__':
  app.run(host='0.0.0.0', port=80)
