targetPosition = nil
knownSects = {}
function update()
  checkTarget()
  
  if (not (targetPosition == nil)) then
    x = targetPosition['x'] - position['x']
    y = targetPosition['y'] - position['y']
    move(x,y)
    mate()
  end
end
function checkTarget()
  for index, id in pairs(knownSects) do
    if distance(id) < 100 then
      targetPosition = positionOf(id)
    end
  end
end
function distance(id)
  p = positionOf(id)
  return math.abs(position['x']-p['x'])
          + math.abs(position['y']-p['y'])
end
function onEntered(data)
  table.insert(knownSects, data['id'])
end
function onLeft(data)
  for index, id in pairs(knownSects) do
    if id == data['id'] then
      table.remove(knownSects, index)
    end
  end
end