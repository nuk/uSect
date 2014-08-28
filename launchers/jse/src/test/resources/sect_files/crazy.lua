targetPosition = nil
knownPeople = {}
attackMode = false
function update()
	changeTarget()
	if targetPosition == nil then
		targetPosition = position
	end
	if(	targetPosition['x'] == position['x'] and 
		targetPosition['y'] == position['y']  )then
		targetPosition['x'] = position['x'] + math.random(-20,20)
		targetPosition['y'] = position['y'] + math.random(-20,20)
		attackMode = false
	end			
	x = targetPosition['x'] - position['x']
	y = targetPosition['y'] - position['y']
	move(x,y)
	if attackMode then
		attack()
	end 
end
function onEntered(data)
	table.insert(knownPeople, data['id'])
end
function onLeft(data)
	for index, id in pairs(knownPeople) do
		if id == data['id'] then
			table.remove(knownPeople, index)
		end
	end
end
function distance(id)
	p = positionOf(id)
	return math.abs(position['x']-p['x'])+math.abs(position['y']-p['y'])
end
function changeTarget()
	for index, id in pairs(knownPeople) do
		if distance(id) < 100 then
			targetPosition = positionOf(id)
			attackMode = true
		end
	end
end;
